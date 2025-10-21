package upeu.edu.pe.catalog.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicStructureRequestDto {
    @NotBlank(message = "El tipo de estructura es obligatorio")
    private String structureType;
    
    @NotNull(message = "El número de períodos por año es obligatorio")
    @Min(value = 1, message = "Debe haber al menos un período por año")
    private Integer periodsPerYear;
    
    private Map<String, Object> configuration;
}