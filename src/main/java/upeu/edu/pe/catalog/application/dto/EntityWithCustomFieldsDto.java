package upeu.edu.pe.catalog.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EntityWithCustomFieldsDto {
    
    @Builder.Default
    private Map<String, Object> customFields = new HashMap<>();
    
    public void addCustomField(String name, Object value) {
        customFields.put(name, value);
    }
    
    public Object getCustomField(String name) {
        return customFields.get(name);
    }
}