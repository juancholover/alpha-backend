package upeu.edu.pe.catalog.domain.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.catalog.application.dto.CustomFieldDto;
import upeu.edu.pe.catalog.application.dto.CustomFieldValueDto;
import upeu.edu.pe.catalog.application.mapper.CustomFieldMapper;
import upeu.edu.pe.catalog.application.mapper.CustomFieldValueMapper;
import upeu.edu.pe.catalog.domain.services.CustomFieldService;
import upeu.edu.pe.catalog.domain.entities.CustomField;
import upeu.edu.pe.catalog.domain.entities.CustomFieldValue;
import upeu.edu.pe.catalog.domain.entities.Institution;
import upeu.edu.pe.catalog.domain.repositories.CustomFieldRepository;
import upeu.edu.pe.catalog.domain.repositories.CustomFieldValueRepository;
import upeu.edu.pe.catalog.domain.repositories.InstitutionRepository;
import upeu.edu.pe.catalog.shared.exceptions.BusinessException;
import upeu.edu.pe.catalog.shared.exceptions.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import upeu.edu.pe.catalog.domain.validation.CustomFieldValidator;

@ApplicationScoped
public class CustomFieldServiceImpl implements CustomFieldService {
    private static final java.util.Set<String> ALLOWED_FIELD_TYPES = java.util.Set.of(
            "text", "textarea", "number", "decimal", "date", "dropdown",
            "checkbox", "radio", "file", "json", "boolean"
    );

    private final CustomFieldRepository customFieldRepository;
    private final CustomFieldValueRepository customFieldValueRepository;
    private final InstitutionRepository institutionRepository;
    private final CustomFieldMapper customFieldMapper;
    private final CustomFieldValueMapper customFieldValueMapper;
    private final ObjectMapper objectMapper;
    
    @Inject
    public CustomFieldServiceImpl(
            CustomFieldRepository customFieldRepository,
            CustomFieldValueRepository customFieldValueRepository,
            InstitutionRepository institutionRepository,
            CustomFieldMapper customFieldMapper,
            CustomFieldValueMapper customFieldValueMapper,
            ObjectMapper objectMapper) {
        this.customFieldRepository = customFieldRepository;
        this.customFieldValueRepository = customFieldValueRepository;
        this.institutionRepository = institutionRepository;
        this.customFieldMapper = customFieldMapper;
        this.customFieldValueMapper = customFieldValueMapper;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public List<CustomFieldDto> getAllCustomFieldsByInstitution(String institutionCode) {
        Institution institution = getInstitutionByCode(institutionCode);
        return customFieldRepository.findByInstitution(institution).stream()
                .map(customFieldMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CustomFieldDto> getCustomFieldsByEntityType(String institutionCode, String entityType) {
        Institution institution = getInstitutionByCode(institutionCode);
        return customFieldRepository.findByInstitutionAndEntityType(institution, entityType).stream()
                .map(customFieldMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public CustomFieldDto getCustomFieldById(Long id) {
        CustomField customField = customFieldRepository.findById(id);
        if (customField == null) {
            throw new NotFoundException("Campo personalizado no encontrado con ID: " + id);
        }
        return customFieldMapper.toDto(customField);
    }
    
    @Override
    @Transactional
    public CustomFieldDto createCustomField(String institutionCode, CustomFieldDto customFieldDto) {
        Institution institution = getInstitutionByCode(institutionCode);
        // Normalizar nombre de campo (slug-like)
        String normalizedFieldName = CustomFieldValidator.normalizeFieldName(customFieldDto.getFieldName());
        customFieldDto.setFieldName(normalizedFieldName);

        // Validar tipo de campo
        if (!ALLOWED_FIELD_TYPES.contains(customFieldDto.getFieldType())) {
            throw new BusinessException("Tipo de campo no soportado: " + customFieldDto.getFieldType());
        }

        // Validaciones específicas por tipo
        CustomFieldValidator.validateFieldDefinition(customFieldDto);

        // Verificar que no exista otro campo con el mismo nombre para esta institución y tipo de entidad
        if (customFieldRepository.existsByInstitutionAndEntityTypeAndFieldName(
                institution, customFieldDto.getEntityType(), customFieldDto.getFieldName())) {
            throw new BusinessException(
                    "Ya existe un campo con el nombre '" + customFieldDto.getFieldName() + 
                    "' para el tipo de entidad '" + customFieldDto.getEntityType() + 
                    "' en esta institución");
        }
        
        CustomField customField = customFieldMapper.toEntity(customFieldDto, institution);
        customFieldRepository.persist(customField);
        
        return customFieldMapper.toDto(customField);
    }
    
    @Override
    @Transactional
    public CustomFieldDto updateCustomField(Long id, CustomFieldDto customFieldDto) {
        CustomField customField = customFieldRepository.findById(id);
        if (customField == null) {
            throw new NotFoundException("Campo personalizado no encontrado con ID: " + id);
        }
        
        Institution institution = getInstitutionByCode(customField.getInstitution().getCode());
        // Normalizar nombre de campo y validar tipo
        String normalizedFieldName = CustomFieldValidator.normalizeFieldName(customFieldDto.getFieldName());
        customFieldDto.setFieldName(normalizedFieldName);

        if (!ALLOWED_FIELD_TYPES.contains(customFieldDto.getFieldType())) {
            throw new BusinessException("Tipo de campo no soportado: " + customFieldDto.getFieldType());
        }

        CustomFieldValidator.validateFieldDefinition(customFieldDto);
        // Verificar que no exista otro campo con el mismo nombre (excluyendo el actual)
        if (!customField.getFieldName().equals(customFieldDto.getFieldName()) &&
            customFieldRepository.existsByInstitutionAndEntityTypeAndFieldName(
                institution, customFieldDto.getEntityType(), customFieldDto.getFieldName())) {
            throw new BusinessException(
                    "Ya existe otro campo con el nombre '" + customFieldDto.getFieldName() + 
                    "' para el tipo de entidad '" + customFieldDto.getEntityType() + 
                    "' en esta institución");
        }
        
        customFieldMapper.updateEntityFromDto(customFieldDto, customField, institution);
        customFieldRepository.persist(customField);
        
        return customFieldMapper.toDto(customField);
    }
    
    @Override
    @Transactional
    public void deleteCustomField(Long id) {
        CustomField customField = customFieldRepository.findById(id);
        if (customField == null) {
            throw new NotFoundException("Campo personalizado no encontrado con ID: " + id);
        }
        
        // Primero eliminar todos los valores asociados a este campo
        customFieldValueRepository.deleteByCustomField(customField);
        
        // Luego eliminar el campo
        customFieldRepository.delete(customField);
    }
    
    @Override
    public Map<String, Object> getCustomFieldValuesForEntity(String institutionCode, String entityType, Long entityId) {
        Institution institution = getInstitutionByCode(institutionCode);
        
        // Obtener todos los valores de campos personalizados para esta entidad
        List<CustomFieldValue> values = customFieldValueRepository
                .findByEntityIdAndCustomFieldInstitutionAndEntityType(entityId, institution, entityType);
        
        // Convertir a un mapa de nombre de campo -> valor
        Map<String, Object> result = new HashMap<>();
        for (CustomFieldValue value : values) {
            String fieldName = value.getCustomField().getFieldName();
            Object fieldValue = customFieldValueMapper.toDto(value).getValue();
            result.put(fieldName, fieldValue);
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public void saveCustomFieldValuesForEntity(
            String institutionCode, String entityType, Long entityId, Map<String, Object> values) {
        
        Institution institution = getInstitutionByCode(institutionCode);
        
        // Obtener todos los campos personalizados para este tipo de entidad
        List<CustomField> fields = customFieldRepository.findByInstitutionAndEntityType(institution, entityType);
        
        // Para cada campo personalizado, verificar si hay un valor para guardar
        for (CustomField field : fields) {
            String fieldName = field.getFieldName();
            
            // Si el campo es requerido y no hay valor, lanzar excepción
            if (field.isRequired() && (!values.containsKey(fieldName) || values.get(fieldName) == null)) {
                throw new BusinessException("El campo '" + fieldName + "' es obligatorio");
            }
            
            // Si hay un valor para este campo, guardarlo o actualizarlo
            if (values.containsKey(fieldName)) {
                Object value = values.get(fieldName);
                
                // Validar el valor
                if (!validateFieldValue(field.getId(), value)) {
                    throw new BusinessException("El valor para el campo '" + fieldName + "' no es válido");
                }

                // Validaciones adicionales basadas en rules/options (pattern, min/max, choices)
                if (!CustomFieldValidator.validateByRules(field, value)) {
                    throw new BusinessException("El valor para el campo '" + fieldName + "' no cumple las reglas de validación");
                }
                
                // Buscar si ya existe un valor para esta entidad y campo
                CustomFieldValue existingValue = customFieldValueRepository
                        .findByCustomFieldAndEntityId(field, entityId)
                        .orElse(null);
                
                if (existingValue != null) {
                    // Actualizar el valor existente
                    customFieldValueMapper.updateEntityFromDto(
                            new CustomFieldValueDto(existingValue.getId(), field.getId(), entityId, value),
                            existingValue,
                            field);
                    customFieldValueRepository.persist(existingValue);
                } else {
                    // Crear un nuevo valor
                    CustomFieldValue newValue = customFieldValueMapper.toEntity(
                            new CustomFieldValueDto(null, field.getId(), entityId, value),
                            field);
                    customFieldValueRepository.persist(newValue);
                }
            }
        }
    }
    
    @Override
    public boolean validateFieldValue(Long customFieldId, Object value) {
        if (value == null) {
            return true; // Los valores nulos son válidos a menos que el campo sea requerido
        }
        
        CustomField field = customFieldRepository.findById(customFieldId);
        if (field == null) {
            throw new NotFoundException("Campo personalizado no encontrado con ID: " + customFieldId);
        }
        
        // Validar según el tipo de campo
        switch (field.getFieldType()) {
            case "text":
                return value instanceof String;
            case "number":
                return value instanceof Number;
            case "boolean":
                return value instanceof Boolean;
            case "date":
                // Validar formato de fecha
                if (!(value instanceof String)) {
                    return false;
                }
                try {
                    // Intentar parsear la fecha
                    java.time.LocalDate.parse((String) value);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            case "dropdown":
                // Validar que el valor esté entre las opciones
                if (!(value instanceof String)) {
                    return false;
                }

                Map<String, Object> options = new HashMap<>();
                try {
                    options = objectMapper.readValue(field.getOptions(), new TypeReference<Map<String, Object>>() {});
                } catch (Exception e) {
                    return false;
                }

                @SuppressWarnings("unchecked")
                List<String> choices = (List<String>) options.getOrDefault("choices", List.of());
                return choices.contains(value);
                
            case "json":
                // Para JSON, cualquier objeto es válido
                return true;
            case "decimal":
                return (value instanceof Number) || (value instanceof String && CustomFieldValidator.isNumeric((String) value));
            case "textarea":
                return value instanceof String;
            default:
                return true;
        }
    }
    
    private Institution getInstitutionByCode(String code) {
        return institutionRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new NotFoundException("Institución no encontrada: " + code));
    }

    // Validation and normalization delegated to CustomFieldValidator
}