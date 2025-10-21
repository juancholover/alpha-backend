package upeu.edu.pe.catalog.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionRequestDto {
    @NotBlank(message = "El nombre de la institución es obligatorio")
    private String name;
    
    @NotBlank(message = "El código de la institución es obligatorio")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "El código debe contener solo letras mayúsculas y números")
    private String code;
    
    @NotBlank(message = "El país es obligatorio")
    private String country;
    
    private Map<String, Object> configuration;
}