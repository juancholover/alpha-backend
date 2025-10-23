package upeu.edu.pe.catalog.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.catalog.domain.entities.CustomField;
import upeu.edu.pe.catalog.domain.entities.Institution;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomFieldRepository implements PanacheRepository<CustomField> {
    
    public List<CustomField> findByInstitution(Institution institution) {
        return list("institution = ?1", institution);
    }
    
    public List<CustomField> findByInstitutionAndEntityType(Institution institution, String entityType) {
        return list("institution = ?1 and entityType = ?2", institution, entityType);
    }
    
    public Optional<CustomField> findByInstitutionAndEntityTypeAndFieldName(
            Institution institution, String entityType, String fieldName) {
        return find(
            "institution = ?1 and entityType = ?2 and fieldName = ?3",
            institution, entityType, fieldName
        ).firstResultOptional();
    }
    
    public boolean existsByInstitutionAndEntityTypeAndFieldName(
            Institution institution, String entityType, String fieldName) {
        return count("institution = ?1 and entityType = ?2 and fieldName = ?3",
               institution, entityType, fieldName) > 0;
    }
}