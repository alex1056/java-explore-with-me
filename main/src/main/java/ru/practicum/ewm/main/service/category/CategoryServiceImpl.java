package ru.practicum.ewm.main.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.category.CategoryDto;
import ru.practicum.ewm.main.dto.category.CategoryMapper;
import ru.practicum.ewm.main.dto.category.NewCategoryDto;
import ru.practicum.ewm.main.exception.Conflict409Exception;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.helper.Helpers;
import ru.practicum.ewm.main.model.Category;

import ru.practicum.ewm.main.model.event.Event;
import ru.practicum.ewm.main.repository.CategoryRepository;
import ru.practicum.ewm.main.repository.EventRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto saveCategory(NewCategoryDto newCategory) {
        Category categoryToSave = new Category(null, newCategory.getName(), null);
        Optional<Category> namedCategory = repository.checkCategoryName(newCategory.getName());
        if (namedCategory.isPresent()) {
            throw new Conflict409Exception("название категории занято");
        }
        return CategoryMapper.toCategoryDto(repository.save(categoryToSave));
    }

    @Transactional
    @Override
    public void deleteCategory(Long catId) {
        Category category = findCategoryById(catId);
        List<Event> events = eventRepository.findEventByCategoryId(catId);
        if (events.size() > 0) {
            throw new Conflict409Exception("Категория содержит события");
        }
        repository.deleteById(catId);

    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Long catId, NewCategoryDto newCategory) {
        Category categorySaved = findCategoryById(catId);
        Optional<Category> namedCategory = repository.checkCategoryName(newCategory.getName());
        if (namedCategory.isPresent() && categorySaved.getId() != (namedCategory.get().getId())) {
            throw new Conflict409Exception("название категории занято");
        }
        categorySaved.setName(newCategory.getName());
        return CategoryMapper.toCategoryDto(repository.save(categorySaved));
    }

    @Override
    public Category findCategoryById(Long catId) {
        Optional<Category> categoryOpt = repository.findById(catId);
        categoryOpt.orElseThrow(() -> new NotFoundException("категория с id=" + catId));
        return categoryOpt.get();
    }

    @Transactional
    @Override
    public CategoryDto findCategoryByIdPublic(Long catId) {
        return CategoryMapper.toCategoryDto(findCategoryById(catId));
    }

    @Transactional
    @Override
    public List<CategoryDto> findAllCategories(Integer from, Integer size) {
        Pageable page = PageRequest.of(Helpers.getPageNumber(from, size), size);
        List<Category> categoryList = repository.findAll(page).getContent();
        return CategoryMapper.toCategoryDto(categoryList);
    }
}
