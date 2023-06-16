package ru.practicum.ewm.main.dto.category;

import ru.practicum.ewm.main.model.Category;


import java.util.ArrayList;
import java.util.List;


public class CategoryMapper {
    public static CategoryDto toCategoryDto(Category category) {
        if (category == null) return null;
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }

    public static List<CategoryDto> toCategoryDto(Iterable<Category> categories) {
        List<CategoryDto> result = new ArrayList<>();
        for (Category category : categories) {
            result.add(toCategoryDto(category));
        }
        return result;
    }
}
