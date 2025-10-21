package upeu.edu.pe.catalog.domain.repositories.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.catalog.application.dto.AcademicStructureRequestDto;
import upeu.edu.pe.catalog.application.dto.AcademicStructureResponseDto;
import upeu.edu.pe.catalog.application.mapper.AcademicStructureMapper;
import upeu.edu.pe.catalog.domain.entities.AcademicStructure;
import upeu.edu.pe.catalog.domain.entities.Institution;
import upeu.edu.pe.catalog.domain.repositories.AcademicStructureRepository;
import upeu.edu.pe.catalog.domain.repositories.InstitutionRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AcademicStructureServiceImpl implements upeu.edu.pe.catalog.domain.services.AcademicStructureService {

    private final AcademicStructureRepository academicStructureRepository;
    private final InstitutionRepository institutionRepository;
    private final AcademicStructureMapper academicStructureMapper;
    private final ObjectMapper objectMapper;

    @Inject
    public AcademicStructureServiceImpl(
            AcademicStructureRepository academicStructureRepository,
            InstitutionRepository institutionRepository,
            AcademicStructureMapper academicStructureMapper,
            ObjectMapper objectMapper) {
        this.academicStructureRepository = academicStructureRepository;
        this.institutionRepository = institutionRepository;
        this.academicStructureMapper = academicStructureMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    @CacheResult(cacheName = "academicStructures")
    public List<AcademicStructureResponseDto> getAllStructuresByInstitution(String institutionCode) {
        Institution institution = getInstitutionByCode(institutionCode);

        return academicStructureRepository.list("institution.id", institution.getId()).stream()
                .map(academicStructureMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @CacheResult(cacheName = "activeAcademicStructure")
    public AcademicStructureResponseDto getActiveStructure(String institutionCode) {
        Institution institution = getInstitutionByCode(institutionCode);

        AcademicStructure structure = academicStructureRepository.find("institution.id = ?1 and isActive = ?2",
                institution.getId(), true).firstResultOptional()
                .orElseThrow(() -> new NotFoundException("No hay una estructura académica activa para esta institución"));

        return academicStructureMapper.toResponseDto(structure);
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "academicStructures")
    @CacheInvalidateAll(cacheName = "activeAcademicStructure")
    public AcademicStructureResponseDto createStructure(String institutionCode, AcademicStructureRequestDto dto) {
        Institution institution = getInstitutionByCode(institutionCode);

        AcademicStructure structure = academicStructureMapper.toEntity(dto, institution);
        academicStructureRepository.persist(structure);

        return academicStructureMapper.toResponseDto(structure);
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "academicStructures")
    @CacheInvalidateAll(cacheName = "activeAcademicStructure")
    public AcademicStructureResponseDto updateStructure(Long id, AcademicStructureRequestDto dto) {
        AcademicStructure structure = academicStructureRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Estructura académica no encontrada"));

        structure.setStructureType(dto.getStructureType());
        structure.setPeriodsPerYear(dto.getPeriodsPerYear());

        // Actualizar configuración si se proporciona
        if (dto.getConfiguration() != null) {
            try {
                String configJson = objectMapper.writeValueAsString(dto.getConfiguration());
                structure.setConfiguration(configJson);
            } catch (Exception e) {
                throw new BusinessException("Error al procesar la configuración: " + e.getMessage());
            }
        }

        academicStructureRepository.persist(structure);
        return academicStructureMapper.toResponseDto(structure);
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "academicStructures")
    @CacheInvalidateAll(cacheName = "activeAcademicStructure")
    public AcademicStructureResponseDto activateStructure(Long id) {
        AcademicStructure structure = academicStructureRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Estructura académica no encontrada"));

        // Desactivar la estructura activa actual (si existe)
        academicStructureRepository.find("institution.id = ?1 and isActive = ?2",
                structure.getInstitution().getId(), true).firstResultOptional()
                .ifPresent(active -> {
                    active.setActive(false);
                    academicStructureRepository.persist(active);
                });

        // Activar la nueva estructura
        structure.setActive(true);
        academicStructureRepository.persist(structure);

        return academicStructureMapper.toResponseDto(structure);
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "academicStructures")
    @CacheInvalidateAll(cacheName = "activeAcademicStructure")
    public void deleteStructure(Long id) {
        AcademicStructure structure = academicStructureRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Estructura académica no encontrada"));

        if (structure.isActive()) {
            throw new BusinessException("No se puede eliminar una estructura académica activa");
        }

        academicStructureRepository.delete(structure);
    }

    private Institution getInstitutionByCode(String code) {
        return institutionRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new NotFoundException("Institución no encontrada: " + code));
    }
}