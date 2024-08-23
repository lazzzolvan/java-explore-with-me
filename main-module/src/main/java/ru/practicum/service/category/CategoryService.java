package ru.practicum.service.category;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(Long catId);

    CategoryDto create(NewCategoryDto newCategoryDto);

    void delete(Long catId);

    CategoryDto update(Long catId, CategoryDto categoryDto);
}
