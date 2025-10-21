package upeu.edu.pe.catalog.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.catalog.domain.entities.Institution;

import java.util.Optional;

@ApplicationScoped
public class InstitutionRepository implements PanacheRepository<Institution> {

    public Optional<Institution> findByCode(String code) {
        return find("code", code).firstResultOptional();
    }

    public Optional<Institution> findByCodeAndIsActiveTrue(String code) {
        return find("code = ?1 and isActive = ?2", code, true).firstResultOptional();
    }

    public boolean existsByCode(String code) {
        return count("code", code) > 0;
    }
}