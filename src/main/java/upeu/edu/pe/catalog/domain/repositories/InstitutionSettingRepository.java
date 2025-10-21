package upeu.edu.pe.catalog.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.catalog.domain.entities.InstitutionSetting;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class InstitutionSettingRepository implements PanacheRepository<InstitutionSetting> {

    public List<InstitutionSetting> findByInstitutionId(Long institutionId) {
        return list("institution.id", institutionId);
    }

    public List<InstitutionSetting> findByInstitutionIdAndIsPublicTrue(Long institutionId) {
        return list("institution.id = ?1 and isPublic = ?2", institutionId, true);
    }

    public Optional<InstitutionSetting> findByInstitutionIdAndModuleAndSettingKey(
            Long institutionId, String module, String settingKey) {
        return find("institution.id = ?1 and module = ?2 and settingKey = ?3",
                institutionId, module, settingKey).firstResultOptional();
    }

    public List<InstitutionSetting> findByInstitutionIdAndModuleInAndIsPublicTrue(
            Long institutionId, List<String> modules) {
        return list("institution.id = ?1 and module in ?2 and isPublic = ?3",
                institutionId, modules, true);
    }
}