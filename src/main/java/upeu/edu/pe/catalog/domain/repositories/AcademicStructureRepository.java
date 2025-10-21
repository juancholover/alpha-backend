package upeu.edu.pe.catalog.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import upeu.edu.pe.catalog.domain.entities.AcademicStructure;

import java.util.Optional;

public interface AcademicStructureRepository extends PanacheRepository<AcademicStructure> {
    Optional<AcademicStructure> findByInstitutionCodeAndIsActive(String institutionCode, boolean isActive);
}