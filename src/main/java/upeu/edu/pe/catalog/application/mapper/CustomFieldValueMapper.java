package upeu.edu.pe.catalog.application.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.catalog.application.dto.CustomFieldValueDto;
import upeu.edu.pe.catalog.domain.entities.CustomField;
import upeu.edu.pe.catalog.domain.entities.CustomFieldValue;
import upeu.edu.pe.catalog.shared.exceptions.BusinessException;

import java.time.LocalDateTime;
import java.util.Map;

@ApplicationScoped
public class CustomFieldValueMapper {
    
    private final ObjectMapper objectMapper;
    
    @Inject
    public CustomFieldValueMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public CustomFieldValueDto toDto(CustomFieldValue entity) {
        Object value = parseJsonValue(entity.getValue(), entity.getCustomField().getFieldType());
        
        return CustomFieldValueDto.builder()
                .id(entity.getId())
                .customFieldId(entity.getCustomField().getId())
                .entityId(entity.getEntityId())
                .value(value)
                .build();
    }
    
    public CustomFieldValue toEntity(CustomFieldValueDto dto, CustomField customField) {
        String valueJson = toJsonString(dto.getValue(), customField.getFieldType());
        
        LocalDateTime now = LocalDateTime.now();
        
        return CustomFieldValue.builder()
                .customField(customField)
                .entityId(dto.getEntityId())
                .value(valueJson)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
    
    public void updateEntityFromDto(CustomFieldValueDto dto, CustomFieldValue entity, CustomField customField) {
        entity.setCustomField(customField);
        entity.setEntityId(dto.getEntityId());
        entity.setValue(toJsonString(dto.getValue(), customField.getFieldType()));
        entity.setUpdatedAt(LocalDateTime.now());
    }
    
    private Object parseJsonValue(String json, String fieldType) {
        if (json == null || json.isBlank()) {
            return null;
        }
        
        try {
            if ("json".equals(fieldType)) {
                return objectMapper.readValue(json, Object.class);
            } else {
                // Para otros tipos, intentamos extraer el valor del objeto JSON {"value": xxx}
                Map<String, Object> valueMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
                return valueMap.get("value");
            }
        } catch (JsonProcessingException e) {
            return json; // Devolver el JSON como string si hay error
        }
    }
    
    private String toJsonString(Object value, String fieldType) {
        if (value == null) {
            return null;
        }
        
        try {
            if ("json".equals(fieldType)) {
                // Para tipo JSON, guardamos el objeto directamente
                return objectMapper.writeValueAsString(value);
            } else {
                // Para otros tipos, lo encapsulamos en un objeto {"value": xxx}
                Map<String, Object> valueMap = Map.of("value", value);
                return objectMapper.writeValueAsString(valueMap);
            }
        } catch (JsonProcessingException e) {
            throw new BusinessException("Error al convertir a JSON: " + e.getMessage());
        }
    }
}