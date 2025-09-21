// src/main/java/upeu/edu/pe/catalog/application/mapper/CategoryMapper.java
package upeu.edu.pe.catalog.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.catalog.domain.entities.Category;
import upeu.edu.pe.catalog.application.dto.CategoryRequestDto;
import upeu.edu.pe.catalog.application.dto.CategoryResponseDto;
import upeu.edu.pe.catalog.application.dto.CategoryUpdateDto;
import java.util.List;

@Mapper(componentModel = "cdi")
public interface CategoryMapper {

    CategoryResponseDto toResponseDto(Category category);

    List<CategoryResponseDto> toResponseDtoList(List<Category> categories);

    Category toEntity(CategoryRequestDto requestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(CategoryUpdateDto updateDto, @MappingTarget Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category requestDtoToEntity(CategoryRequestDto requestDto);
}