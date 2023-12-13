package ru.practicum.ewm.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dto.CategoryDto;
import ru.practicum.ewm.main.exception.ObjectNotFoundException;
import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryDto createCategory(CategoryDto categoryDto) {
        return categoryMapper.toCategoryDto(
                categoryRepository.save(
                        categoryMapper.toCategory(categoryDto)));
    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return categoryMapper.toCategoryDto(
                categoryRepository.findAll(PageRequestByElement.of(from, size)).toList());
    }

    public CategoryDto getCategoryById(Long id) {
        return categoryMapper.toCategoryDto(
                checkCategory(id));
    }

    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category existingCategory = checkCategory(id);
        existingCategory.setName(categoryDto.getName());
        return categoryMapper.toCategoryDto(
                categoryRepository.save(existingCategory));
    }


    public void deleteCategory(Long id) {
        checkCategory(id);
        categoryRepository.deleteById(id);
    }

    private Category checkCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id категории: " + id));
    }
}
