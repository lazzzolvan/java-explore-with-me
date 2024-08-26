package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.category.Category;
import ru.practicum.repository.categories.CategoriesRepository;
import ru.practicum.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoriesRepository categoriesRepository;
    private final EventRepository eventsRepository;

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        return categoriesRepository.findAll(PageRequest.of(from, size)).stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Long id) {
        Category category = findById(id);
        return CategoryMapper.toDto(category);
    }

    private Category findById(Long id) {
        return categoriesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category not found with id: ", id)));
    }

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        try {
            Category category = categoriesRepository.save(new Category(newCategoryDto.getName()));
            return new CategoryDto(category.getId(), category.getName());
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(String.format("Category with name: %s already exists", newCategoryDto.getName()));
        }
    }

    @Override
    @Transactional
    public void delete(Long catId) {
            categoriesRepository.delete(findById(catId));
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, CategoryDto categoryDto) {
        Category category = findById(catId);

        Optional<Category> existingCategory = categoriesRepository.findFirstByName(categoryDto.getName());
        if (existingCategory.isPresent() && !existingCategory.get().getId().equals(catId)) {
            throw new ConflictException("Категория с таким именем уже существует");
        }

        category.setName(categoryDto.getName());
        return CategoryMapper.toDto(category);
    }
}