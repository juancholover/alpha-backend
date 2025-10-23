package upeu.edu.pe.catalog.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.catalog.domain.entities.CustomField;
import upeu.edu.pe.catalog.domain.entities.CustomFieldValue;
import upeu.edu.pe.catalog.domain.entities.Institution;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomFieldValueRepository implements PanacheRepository<CustomFieldValue> {
    
    public List<CustomFieldValue> findByEntityIdAndCustomFieldEntityType(Long entityId, String entityType) {
        return list("entityId = ?1 and customField.entityType = ?2", entityId, entityType);
    }
    
    public List<CustomFieldValue> findByEntityIdAndCustomFieldInstitution(Long entityId, Institution institution) {
        return list("entityId = ?1 and customField.institution = ?2", entityId, institution);
    }
    
    public List<CustomFieldValue> findByEntityIdAndCustomFieldInstitutionAndEntityType(
            Long entityId, Institution institution, String entityType) {
        return list(
            "entityId = ?1 and customField.institution = ?2 and customField.entityType = ?3",
            entityId, institution, entityType
        );
    }
    
    public Optional<CustomFieldValue> findByCustomFieldAndEntityId(CustomField customField, Long entityId) {
        return find("customField = ?1 and entityId = ?2", customField, entityId).firstResultOptional();
    }
    
    public void deleteByCustomField(CustomField customField) {
        delete("customField", customField);
    }
    
    public void deleteByCustomFieldAndEntityId(CustomField customField, Long entityId) {
        delete("customField = ?1 and entityId = ?2", customField, entityId);
    }
}