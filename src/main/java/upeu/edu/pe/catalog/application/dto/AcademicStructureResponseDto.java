package upeu.edu.pe.catalog.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicStructureResponseDto {
    private Long id;
    private Long institutionId;
    private String institutionName;
    private String structureType;
    private Integer periodsPerYear;
    private Map<String, Object> configuration;
    private boolean isActive;
    private LocalDateTime createdAt;
}