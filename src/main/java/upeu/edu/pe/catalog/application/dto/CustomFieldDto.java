package upeu.edu.pe.catalog.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldDto {
    
    private Long id;
    
    @NotNull(message = "La institución es obligatoria")
    private Long institutionId;
    
    @NotBlank(message = "El tipo de entidad es obligatorio")
    private String entityType;
    
    @NotBlank(message = "El nombre del campo es obligatorio")
    private String fieldName;
    
    @NotBlank(message = "El tipo de campo es obligatorio")
    private String fieldType;
    
    @NotNull(message = "Debe especificar si el campo es obligatorio")
    private Boolean isRequired;
    
    private Map<String, Object> options;
    
    private Map<String, Object> validationRules;
    
    private Integer displayOrder;
}