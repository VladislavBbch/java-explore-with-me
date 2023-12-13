package ru.practicum.ewm.main.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.CategoryDto;
import ru.practicum.ewm.main.service.CategoryService;

import javax.validation.Valid;

import static ru.practicum.ewm.main.controller.Constant.ADMIN_URL_PREFIX;

@RestController
@RequestMapping(path = ADMIN_URL_PREFIX + "/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Начало обработки запроса на создание категории: {}", categoryDto);
        CategoryDto newCategory = categoryService.createCategory(categoryDto);
        log.info("Окончание обработки запроса на создание категории");
        return newCategory;
    }

    @PatchMapping("/{id}")
    public CategoryDto updateCategory(@PathVariable Long id,
                                      @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Начало обработки запроса на обновление категории: {}", id);
        CategoryDto existingCategory = categoryService.updateCategory(id, categoryDto);
        log.info("Окончание обработки запроса на обновление категории");
        return existingCategory;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        log.info("Начало обработки запроса на удаление категории: {}", id);
        categoryService.deleteCategory(id);
        log.info("Окончание обработки запроса на удаление категории");
    }
}
