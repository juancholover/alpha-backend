package upeu.edu.pe.catalog.domain.services;

public interface EntityExistenceService {
    /**
     * Check if an entity of given type exists with the provided id.
     * @param entityType the logical entity type (e.g. "category")
     * @param id the entity id to check
     * @return true if exists, false otherwise
     */
    boolean exists(String entityType, Long id);

    /**
     * Check if the given logical entity type is supported by the existence service.
     * e.g. "category" -> true, "product" -> false (until implemented)
     */
    boolean isSupportedEntityType(String entityType);
}
