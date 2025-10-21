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
public class LoginConfigDto {
    private String name;
    private String code;
    private String logo;
    private Map<String, Object> colors;
    private String slogan;
    private String background;
    private Map<String, Boolean> authMethods;
}