package upeu.edu.pe.catalog.application.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.catalog.application.dto.InstitutionConfigDto;
import upeu.edu.pe.catalog.application.dto.InstitutionRequestDto;
import upeu.edu.pe.catalog.application.dto.InstitutionResponseDto;
import upeu.edu.pe.catalog.domain.entities.Institution;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class InstitutionMapper {

    private final ObjectMapper objectMapper;

    @Inject
    public InstitutionMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public InstitutionResponseDto toResponseDto(Institution institution) {
        Map<String, Object> config;
        try {
            config = institution.getConfiguration() != null ? 
                    objectMapper.readValue(institution.getConfiguration(), new TypeReference<Map<String, Object>>() {}) : 
                    new HashMap<>();
        } catch (JsonProcessingException e) {
            config = new HashMap<>();
        }
        
        return InstitutionResponseDto.builder()
                .id(institution.getId())
                .name(institution.getName())
                .code(institution.getCode())
                .country(institution.getCountry())
                .configuration(config)
                .createdAt(institution.getCreatedAt())
                .isActive(institution.isActive())
                .build();
    }
    
    public InstitutionConfigDto toConfigDto(Institution institution, Map<String, Object> settings) {
        Map<String, Object> config;
        try {
            config = institution.getConfiguration() != null ? 
                    objectMapper.readValue(institution.getConfiguration(), new TypeReference<Map<String, Object>>() {}) : 
                    new HashMap<>();
        } catch (JsonProcessingException e) {
            config = new HashMap<>();
        }
        
        return InstitutionConfigDto.builder()
                .id(institution.getId())
                .name(institution.getName())
                .code(institution.getCode())
                .country(institution.getCountry())
                .configuration(config)
                .settings(settings)
                .build();
    }
    
    public Institution toEntity(InstitutionRequestDto dto) {
        String configJson = null;
        if (dto.getConfiguration() != null) {
            try {
                configJson = objectMapper.writeValueAsString(dto.getConfiguration());
            } catch (JsonProcessingException e) {
                // Manejar excepción
            }
        }
        
        return Institution.builder()
                .name(dto.getName())
                .code(dto.getCode().toUpperCase())
                .country(dto.getCountry())
                .configuration(configJson)
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .build();
    }
}