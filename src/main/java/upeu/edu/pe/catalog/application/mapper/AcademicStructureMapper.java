package upeu.edu.pe.catalog.application.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.catalog.application.dto.AcademicStructureRequestDto;
import upeu.edu.pe.catalog.application.dto.AcademicStructureResponseDto;
import upeu.edu.pe.catalog.domain.entities.AcademicStructure;
import upeu.edu.pe.catalog.domain.entities.Institution;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class AcademicStructureMapper {

    private final ObjectMapper objectMapper;

    @Inject
    public AcademicStructureMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AcademicStructureResponseDto toResponseDto(AcademicStructure structure) {
        Map<String, Object> config;
        try {
            config = structure.getConfiguration() != null ?
                    objectMapper.readValue(structure.getConfiguration(), new TypeReference<Map<String, Object>>() {}) :
                    new HashMap<>();
        } catch (JsonProcessingException e) {
            config = new HashMap<>();
        }

        return AcademicStructureResponseDto.builder()
                .id(structure.getId())
                .institutionId(structure.getInstitution().getId())
                .institutionName(structure.getInstitution().getName())
                .structureType(structure.getStructureType())
                .periodsPerYear(structure.getPeriodsPerYear())
                .configuration(config)
                .isActive(structure.isActive())
                .createdAt(structure.getCreatedAt())
                .build();
    }

    public AcademicStructure toEntity(AcademicStructureRequestDto dto, Institution institution) {
        String configJson = null;
        if (dto.getConfiguration() != null) {
            try {
                configJson = objectMapper.writeValueAsString(dto.getConfiguration());
            } catch (JsonProcessingException e) {
                // Manejar excepción
            }
        }

        return AcademicStructure.builder()
                .structureType(dto.getStructureType())
                .periodsPerYear(dto.getPeriodsPerYear())
                .configuration(configJson)
                .institution(institution)
                .isActive(false) // Por defecto inactiva
                .createdAt(LocalDateTime.now())
                .build();
    }
}