package upeu.edu.pe.catalog.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.catalog.domain.entities.Category;
import upeu.edu.pe.catalog.application.dto.CategoryRequestDto;
import upeu.edu.pe.catalog.application.dto.CategoryResponseDto;
import upeu.edu.pe.catalog.application.dto.CategoryUpdateDto;
import java.util.List;

@Mapper(componentModel = "cdi",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    CategoryResponseDto toResponseDto(Category category);

    List<CategoryResponseDto> toResponseDtoList(List<Category> categories);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    Category toEntity(CategoryRequestDto requestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(CategoryUpdateDto updateDto, @MappingTarget Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category requestDtoToEntity(CategoryRequestDto requestDto);
}