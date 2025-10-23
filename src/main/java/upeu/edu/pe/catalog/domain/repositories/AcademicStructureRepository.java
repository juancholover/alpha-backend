package upeu.edu.pe.catalog.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.catalog.domain.entities.AcademicStructure;

import java.util.Optional;

@ApplicationScoped
public class AcademicStructureRepository implements PanacheRepository<AcademicStructure> {

    public Optional<AcademicStructure> findByInstitutionCodeAndIsActive(String institutionCode, boolean isActive) {
        return find("institution.code = ?1 and isActive = ?2", institutionCode, isActive).firstResultOptional();
    }
}