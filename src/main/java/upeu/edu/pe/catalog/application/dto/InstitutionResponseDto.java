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
public class InstitutionResponseDto {
    private Long id;
    private String name;
    private String code;
    private String country;
    private Map<String, Object> configuration;
    private LocalDateTime createdAt;
    private boolean isActive;
}