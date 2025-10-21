package upeu.edu.pe.catalog.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionSettingDto {
    private Long id;
    
    @NotBlank(message = "El módulo es obligatorio")
    private String module;
    
    @NotBlank(message = "La clave de configuración es obligatoria")
    private String settingKey;
    
    private String settingValue;
    
    @NotBlank(message = "El tipo de dato es obligatorio")
    private String dataType;
    
    private String description;
    
    @NotNull(message = "El campo isPublic es obligatorio")
    private Boolean isPublic;
}