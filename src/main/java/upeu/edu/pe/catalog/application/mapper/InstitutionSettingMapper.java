package upeu.edu.pe.catalog.application.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.catalog.application.dto.InstitutionSettingDto;
import upeu.edu.pe.catalog.domain.entities.Institution;
import upeu.edu.pe.catalog.domain.entities.InstitutionSetting;

import java.time.LocalDateTime;

@ApplicationScoped
public class InstitutionSettingMapper {

    private final ObjectMapper objectMapper;

    @Inject
    public InstitutionSettingMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public InstitutionSettingDto toDto(InstitutionSetting setting) {
        return InstitutionSettingDto.builder()
                .id(setting.getId())
                .module(setting.getModule())
                .settingKey(setting.getSettingKey())
                .settingValue(setting.getSettingValue())
                .dataType(setting.getDataType())
                .description(setting.getDescription())
                .isPublic(setting.getIsPublic())
                .build();
    }
    
    public InstitutionSetting toEntity(InstitutionSettingDto dto, Institution institution) {
        String value = dto.getSettingValue();
        
        // Si es tipo JSON y no es un string de JSON, convertir
        if ("json".equals(dto.getDataType()) && value != null && !value.startsWith("{") && !value.startsWith("[")) {
            try {
                value = objectMapper.writeValueAsString(dto.getSettingValue());
            } catch (JsonProcessingException e) {
                // Mantener el valor original
            }
        }
        
        return InstitutionSetting.builder()
                .institution(institution)
                .module(dto.getModule())
                .settingKey(dto.getSettingKey())
                .settingValue(value)
                .dataType(dto.getDataType())
                .description(dto.getDescription())
                .isPublic(dto.getIsPublic())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    public void updateEntityFromDto(InstitutionSettingDto dto, InstitutionSetting setting) {
        setting.setModule(dto.getModule());
        setting.setSettingKey(dto.getSettingKey());
        
        String value = dto.getSettingValue();
        
        // Si es tipo JSON y no es un string de JSON, convertir
        if ("json".equals(dto.getDataType()) && value != null && !value.startsWith("{") && !value.startsWith("[")) {
            try {
                value = objectMapper.writeValueAsString(dto.getSettingValue());
            } catch (JsonProcessingException e) {
                // Mantener el valor original
            }
        }
        
        setting.setSettingValue(value);
        setting.setDataType(dto.getDataType());
        setting.setDescription(dto.getDescription());
        setting.setIsPublic(dto.getIsPublic());
        setting.setUpdatedAt(LocalDateTime.now());
    }
}