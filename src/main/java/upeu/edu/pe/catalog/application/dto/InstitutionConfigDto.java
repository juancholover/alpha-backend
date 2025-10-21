package upeu.edu.pe.catalog.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionConfigDto {
    private Long id;
    private String name;
    private String code;
    private String country;
    private Map<String, Object> configuration;
    private Map<String, Object> settings;
}