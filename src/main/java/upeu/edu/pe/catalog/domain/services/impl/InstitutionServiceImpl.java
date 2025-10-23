package upeu.edu.pe.catalog.domain.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.catalog.application.dto.*;
import upeu.edu.pe.catalog.application.mapper.InstitutionMapper;
import upeu.edu.pe.catalog.application.mapper.InstitutionSettingMapper;
import upeu.edu.pe.catalog.domain.services.InstitutionService;
import upeu.edu.pe.catalog.domain.entities.Institution;
import upeu.edu.pe.catalog.domain.entities.InstitutionSetting;
import upeu.edu.pe.catalog.domain.repositories.InstitutionRepository;
import upeu.edu.pe.catalog.domain.repositories.InstitutionSettingRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final InstitutionSettingRepository settingRepository;
    private final InstitutionMapper institutionMapper;
    private final InstitutionSettingMapper settingMapper;
    private final ObjectMapper objectMapper;

    @Inject
    public InstitutionServiceImpl(
            InstitutionRepository institutionRepository,
            InstitutionSettingRepository settingRepository,
            InstitutionMapper institutionMapper,
            InstitutionSettingMapper settingMapper,
            ObjectMapper objectMapper) {
        this.institutionRepository = institutionRepository;
        this.settingRepository = settingRepository;
        this.institutionMapper = institutionMapper;
        this.settingMapper = settingMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<InstitutionResponseDto> getAllInstitutions() {
        return institutionRepository.listAll().stream()
                .map(institutionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @CacheResult(cacheName = "institutions")
    public InstitutionResponseDto getInstitution(String code) {
        Institution institution = institutionRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Institution not found: " + code));

        return institutionMapper.toResponseDto(institution);
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "institutions")
    @CacheInvalidateAll(cacheName = "institutionConfigs")
    @CacheInvalidateAll(cacheName = "loginConfigs")
    public InstitutionResponseDto createInstitution(InstitutionRequestDto institutionDto) {
        if (institutionRepository.existsByCode(institutionDto.getCode())) {
            throw new BusinessException("Institution code already exists: " + institutionDto.getCode());
        }

        Institution institution = institutionMapper.toEntity(institutionDto);
        institutionRepository.persist(institution);

        return institutionMapper.toResponseDto(institution);
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "institutions")
    @CacheInvalidateAll(cacheName = "institutionConfigs")
    @CacheInvalidateAll(cacheName = "loginConfigs")
    public InstitutionResponseDto updateInstitution(String code, InstitutionRequestDto institutionDto) {
        Institution institution = institutionRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Institution not found: " + code));

        institution.setName(institutionDto.getName());
        institution.setCountry(institutionDto.getCountry());

        if (institutionDto.getConfiguration() != null) {
            try {
                institution.setConfiguration(objectMapper.writeValueAsString(institutionDto.getConfiguration()));
            } catch (JsonProcessingException e) {
                throw new BusinessException("Error processing configuration JSON: " + e.getMessage());
            }
        }

        institutionRepository.persist(institution);
        return institutionMapper.toResponseDto(institution);
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "institutions")
    @CacheInvalidateAll(cacheName = "institutionConfigs")
    @CacheInvalidateAll(cacheName = "loginConfigs")
    public void deleteInstitution(String code) {
        Institution institution = institutionRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Institution not found: " + code));

        institution.setActive(false);
        institutionRepository.persist(institution);
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "institutions")
    @CacheInvalidateAll(cacheName = "institutionConfigs")
    @CacheInvalidateAll(cacheName = "loginConfigs")
    public void activateInstitution(String code) {
        Institution institution = institutionRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Institution not found: " + code));

        institution.setActive(true);
        institutionRepository.persist(institution);
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "institutions")
    @CacheInvalidateAll(cacheName = "institutionConfigs")
    @CacheInvalidateAll(cacheName = "loginConfigs")
    public void deactivateInstitution(String code) {
        Institution institution = institutionRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Institution not found: " + code));

        institution.setActive(false);
        institutionRepository.persist(institution);
    }

    @Override
    @CacheResult(cacheName = "institutionConfigs")
    public InstitutionConfigDto getInstitutionConfig(String code) {
        Institution institution = institutionRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new NotFoundException("Active institution not found: " + code));

        List<InstitutionSetting> settings = settingRepository.findByInstitutionIdAndIsPublicTrue(institution.getId());

        Map<String, Object> formattedSettings = formatSettings(settings);

        return institutionMapper.toConfigDto(institution, formattedSettings);
    }

    @Override
    @CacheResult(cacheName = "loginConfigs")
    public LoginConfigDto getLoginConfig(String code) {
        Institution institution = institutionRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new NotFoundException("Active institution not found: " + code));

        List<InstitutionSetting> settings = settingRepository.findByInstitutionIdAndModuleInAndIsPublicTrue(
                institution.getId(), Arrays.asList("appearance", "auth"));

        Map<String, Object> formattedSettings = formatSettings(settings);
        Map<String, Object> config;

        try {
            config = institution.getConfiguration() != null ?
                    objectMapper.readValue(institution.getConfiguration(), new TypeReference<Map<String, Object>>() {}) :
                    new HashMap<>();
        } catch (JsonProcessingException e) {
            config = new HashMap<>();
        }

        // Extraer valores específicos para el login
        @SuppressWarnings("unchecked")
        Map<String, Object> colors = (Map<String, Object>) config.getOrDefault("colors",
                formattedSettings.getOrDefault("appearance.theme_colors", new HashMap<>()));

        @SuppressWarnings("unchecked")
        Map<String, Object> branding = (Map<String, Object>) formattedSettings.getOrDefault("appearance.branding", new HashMap<>());
        String logo = (String) config.getOrDefault("logo", branding.getOrDefault("logo_url", null));

        String slogan = (String) config.getOrDefault("slogan", branding.getOrDefault("slogan", null));

        String background = (String) formattedSettings.getOrDefault("appearance.login_background",
                "/assets/images/upeu-campus-background.jpg");

        @SuppressWarnings("unchecked")
        Map<String, Boolean> authMethods = (Map<String, Boolean>) formattedSettings.getOrDefault("auth.methods",
                Map.of("local", true, "microsoft", true));

        return LoginConfigDto.builder()
                .name(institution.getName())
                .code(institution.getCode())
                .logo(logo)
                .colors(colors)
                .slogan(slogan)
                .background(background)
                .authMethods(authMethods)
                .build();
    }

    @Override
    public List<InstitutionSettingDto> getInstitutionSettings(String code) {
        Institution institution = institutionRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new NotFoundException("Active institution not found: " + code));

        return settingRepository.findByInstitutionId(institution.getId()).stream()
                .map(settingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public InstitutionSettingDto getInstitutionSetting(String code, String module, String key) {
        Institution institution = institutionRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new NotFoundException("Active institution not found: " + code));

        InstitutionSetting setting = settingRepository.findByInstitutionIdAndModuleAndSettingKey(
                institution.getId(), module, key)
                .orElseThrow(() -> new NotFoundException(
                        "Setting not found for institution: " + code + ", module: " + module + ", key: " + key));

        return settingMapper.toDto(setting);
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "institutionConfigs")
    @CacheInvalidateAll(cacheName = "loginConfigs")
    public InstitutionSettingDto createInstitutionSetting(String code, InstitutionSettingDto settingDto) {
        Institution institution = institutionRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new NotFoundException("Active institution not found: " + code));

        // Verificar si ya existe esta configuración
        Optional<InstitutionSetting> existingSetting = settingRepository.findByInstitutionIdAndModuleAndSettingKey(
                institution.getId(), settingDto.getModule(), settingDto.getSettingKey());

        if (existingSetting.isPresent()) {
            throw new BusinessException("Setting already exists for this institution, module and key");
        }

        InstitutionSetting setting = settingMapper.toEntity(settingDto, institution);
        settingRepository.persist(setting);

        return settingMapper.toDto(setting);
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "institutionConfigs")
    @CacheInvalidateAll(cacheName = "loginConfigs")
    public InstitutionSettingDto updateInstitutionSetting(Long id, InstitutionSettingDto settingDto) {
        InstitutionSetting setting = settingRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Setting not found with ID: " + id));

        settingMapper.updateEntityFromDto(settingDto, setting);
        settingRepository.persist(setting);

        return settingMapper.toDto(setting);
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "institutionConfigs")
    @CacheInvalidateAll(cacheName = "loginConfigs")
    public void deleteInstitutionSetting(Long id) {
        if (!settingRepository.deleteById(id)) {
            throw new NotFoundException("Setting not found with ID: " + id);
        }
    }

    private Map<String, Object> formatSettings(List<InstitutionSetting> settings) {
        return settings.stream().collect(Collectors.toMap(
                setting -> setting.getModule() + "." + setting.getSettingKey(),
                setting -> {
                    String value = setting.getSettingValue();
                    try {
                        if ("json".equals(setting.getDataType()) && value != null) {
                            return objectMapper.readValue(value, Object.class);
                        } else if ("boolean".equals(setting.getDataType())) {
                            return Boolean.parseBoolean(value);
                        } else if ("number".equals(setting.getDataType())) {
                            return Double.parseDouble(value);
                        }
                    } catch (Exception e) {
                        // En caso de error, devolver el valor como string
                    }
                    return value;
                },
                // En caso de duplicados (no debería ocurrir), quedarse con el último
                (existing, replacement) -> replacement
        ));
    }
}