// src/main/java/upeu/edu/pe/catalog/domain/services/CategoryService.java
package upeu.edu.pe.catalog.domain.services;

import upeu.edu.pe.catalog.application.dto.CategoryRequestDto;
import upeu.edu.pe.catalog.application.dto.CategoryResponseDto;
import upeu.edu.pe.catalog.application.dto.CategoryUpdateDto;
import java.util.List;

public interface CategoryService {
    List<CategoryResponseDto> findAll();
    CategoryResponseDto findById(Long id);
    CategoryResponseDto create(CategoryRequestDto requestDto);
    CategoryResponseDto update(Long id, CategoryUpdateDto updateDto);
    void deleteById(Long id);
}