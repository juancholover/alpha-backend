package upeu.edu.pe.catalog.application.services;

import upeu.edu.pe.catalog.application.dto.*;

import java.util.List;

public interface InstitutionService {
    List<InstitutionResponseDto> getAllInstitutions();
    InstitutionResponseDto getInstitution(String code);
    InstitutionResponseDto createInstitution(InstitutionRequestDto institutionDto);
    InstitutionResponseDto updateInstitution(String code, InstitutionRequestDto institutionDto);
    void deleteInstitution(String code);
    void activateInstitution(String code);
    void deactivateInstitution(String code);
    InstitutionConfigDto getInstitutionConfig(String code);
    LoginConfigDto getLoginConfig(String code);
    List<InstitutionSettingDto> getInstitutionSettings(String code);
    InstitutionSettingDto getInstitutionSetting(String code, String module, String key);
    InstitutionSettingDto createInstitutionSetting(String code, InstitutionSettingDto settingDto);
    InstitutionSettingDto updateInstitutionSetting(Long id, InstitutionSettingDto settingDto);
    void deleteInstitutionSetting(Long id);
}