package ru.practicum.ewm.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.service.category.CategoryService;
import ru.practicum.ewm.main.service.stat.StatService;

import javax.validation.constraints.Min;

import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Validated
public class CategoriesPubController {

    private final CategoryService categoryService;
    private final StatService statService;

    @GetMapping("/{catId}")
    public CategoryDto findCategory(@PathVariable Long catId) {
        statService.saveHit("/categories/" + catId);
        return categoryService.findCategoryByIdPublic(catId);
    }

    @GetMapping
    public List<CategoryDto> findAllCategories(
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size
    ) {
        statService.saveHit("/categories");
        return categoryService.findAllCategories(from, size);
    }
}
