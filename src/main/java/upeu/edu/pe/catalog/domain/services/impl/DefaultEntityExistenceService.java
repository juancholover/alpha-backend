package upeu.edu.pe.catalog.domain.services.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.catalog.domain.repositories.CategoryRepository;
import upeu.edu.pe.catalog.domain.services.EntityExistenceService;

@ApplicationScoped
public class DefaultEntityExistenceService implements EntityExistenceService {

    private final CategoryRepository categoryRepository;

    @Inject
    public DefaultEntityExistenceService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public boolean exists(String entityType, Long id) {
        if (entityType == null || id == null) return false;
        switch (entityType.toLowerCase()) {
            case "category":
                return categoryRepository.getCategoryById(id).isPresent();
            // Add more entities here as your domain grows
            default:
                return false;
        }
    }

    @Override
    public boolean isSupportedEntityType(String entityType) {
        if (entityType == null) return false;
        switch (entityType.toLowerCase()) {
            case "category":
                return true;
            // add more supported types here as implemented
            default:
                return false;
        }
    }
}
