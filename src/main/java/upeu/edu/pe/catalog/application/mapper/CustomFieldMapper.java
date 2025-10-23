package upeu.edu.pe.catalog.application.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.catalog.application.dto.CustomFieldDto;
import upeu.edu.pe.catalog.domain.entities.CustomField;
import upeu.edu.pe.catalog.domain.entities.Institution;
import upeu.edu.pe.catalog.shared.exceptions.BusinessException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class CustomFieldMapper {
    
    private final ObjectMapper objectMapper;
    
    @Inject
    public CustomFieldMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public CustomFieldDto toDto(CustomField entity) {
        Map<String, Object> options = parseJsonField(entity.getOptions());
        Map<String, Object> validationRules = parseJsonField(entity.getValidationRules());
        
    return CustomFieldDto.builder()
        .id(entity.getId())
        .institutionId(entity.getInstitution() != null ? entity.getInstitution().getId() : null)
                .entityType(entity.getEntityType())
                .fieldName(entity.getFieldName())
                .fieldType(entity.getFieldType())
                .isRequired(entity.isRequired())
                .options(options)
                .validationRules(validationRules)
                .displayOrder(entity.getDisplayOrder())
                .build();
    }
    
    public CustomField toEntity(CustomFieldDto dto, Institution institution) {
        String optionsJson = toJsonString(dto.getOptions());
        String validationRulesJson = toJsonString(dto.getValidationRules());
        
        LocalDateTime now = LocalDateTime.now();
        
        return CustomField.builder()
                .institution(institution)
                .entityType(dto.getEntityType())
                .fieldName(dto.getFieldName())
                .fieldType(dto.getFieldType())
                .isRequired(Boolean.TRUE.equals(dto.getIsRequired()))
                .options(optionsJson)
                .validationRules(validationRulesJson)
                .displayOrder(dto.getDisplayOrder())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
    
    public void updateEntityFromDto(CustomFieldDto dto, CustomField entity, Institution institution) {
        entity.setInstitution(institution);
        entity.setEntityType(dto.getEntityType());
        entity.setFieldName(dto.getFieldName());
        entity.setFieldType(dto.getFieldType());
        entity.setRequired(Boolean.TRUE.equals(dto.getIsRequired()));
        entity.setOptions(toJsonString(dto.getOptions()));
        entity.setValidationRules(toJsonString(dto.getValidationRules()));
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setUpdatedAt(LocalDateTime.now());
    }
    
    private Map<String, Object> parseJsonField(String json) {
        if (json == null || json.isBlank()) {
            return new HashMap<>();
        }
        
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }
    
    private String toJsonString(Object object) {
        if (object == null) {
            return null;
        }
        
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Error al convertir a JSON: " + e.getMessage());
        }
    }
}