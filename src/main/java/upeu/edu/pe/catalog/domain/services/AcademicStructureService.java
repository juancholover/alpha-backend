package upeu.edu.pe.catalog.domain.services;

import upeu.edu.pe.catalog.application.dto.AcademicStructureRequestDto;
import upeu.edu.pe.catalog.application.dto.AcademicStructureResponseDto;

import java.util.List;

public interface AcademicStructureService {
    
    List<AcademicStructureResponseDto> getAllStructuresByInstitution(String institutionCode);
    
    AcademicStructureResponseDto getActiveStructure(String institutionCode);
    
    AcademicStructureResponseDto createStructure(String institutionCode, AcademicStructureRequestDto dto);
    
    AcademicStructureResponseDto updateStructure(Long id, AcademicStructureRequestDto dto);
    
    AcademicStructureResponseDto activateStructure(Long id);
    
    void deleteStructure(Long id);
}