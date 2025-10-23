package upeu.edu.pe.catalog.domain.services;

import upeu.edu.pe.catalog.application.dto.CustomFieldDto;

import java.util.List;
import java.util.Map;

public interface CustomFieldService {
    
    List<CustomFieldDto> getAllCustomFieldsByInstitution(String institutionCode);
    
    List<CustomFieldDto> getCustomFieldsByEntityType(String institutionCode, String entityType);
    
    CustomFieldDto getCustomFieldById(Long id);
    
    CustomFieldDto createCustomField(String institutionCode, CustomFieldDto customFieldDto);
    
    CustomFieldDto updateCustomField(Long id, CustomFieldDto customFieldDto);
    
    void deleteCustomField(Long id);
    
    // Método para obtener todos los valores de campos personalizados para una entidad
    Map<String, Object> getCustomFieldValuesForEntity(String institutionCode, String entityType, Long entityId);
    
    // Método para guardar múltiples valores de campos personalizados para una entidad
    void saveCustomFieldValuesForEntity(String institutionCode, String entityType, Long entityId, 
                                       Map<String, Object> values);
    
    // Método para validar si un valor cumple con las reglas del campo personalizado
    boolean validateFieldValue(Long customFieldId, Object value);
}