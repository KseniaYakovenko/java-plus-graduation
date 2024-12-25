package ru.practicum.service;

import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.category.CategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getAll(int from, int size);

    CategoryDto getById(Long categoryId);

    void delete(long categoryId);

    CategoryDto createCategory(NewCategoryDto dto);

    CategoryDto updateCategory(CategoryDto dto);
}
