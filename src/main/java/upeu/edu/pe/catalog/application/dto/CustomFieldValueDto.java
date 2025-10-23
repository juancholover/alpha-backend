package upeu.edu.pe.catalog.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldValueDto {
    
    private Long id;
    
    @NotNull(message = "El ID del campo personalizado es obligatorio")
    private Long customFieldId;
    
    @NotNull(message = "El ID de la entidad es obligatorio")
    private Long entityId;
    
    private Object value;
}