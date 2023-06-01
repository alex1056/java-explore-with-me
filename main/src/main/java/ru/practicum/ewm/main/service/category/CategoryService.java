package ru.practicum.ewm.main.service.category;

import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.dto.category.NewCategoryDto;
import ru.practicum.ewm.main.model.Category;


import java.util.List;

public interface CategoryService {

    CategoryDto saveCategory(NewCategoryDto newCategory);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, NewCategoryDto newCategory);

    CategoryDto findCategoryByIdPublic(Long catId);

    List<CategoryDto> findAllCategories(Integer from, Integer size);

    Category findCategoryById(Long catId);
}
