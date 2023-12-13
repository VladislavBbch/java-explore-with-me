package ru.practicum.ewm.main.controller.unauthorized;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.CategoryDto;
import ru.practicum.ewm.main.service.CategoryService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(defaultValue = "10") @Min(1) @Max(1000) Integer size) {
        log.info("Начало обработки запроса по получению категорий");
        List<CategoryDto> categories = categoryService.getCategories(from, size);
        log.info("Окончание обработки запроса по получению категорий");
        return categories;
    }

    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        log.info("Начало обработки запроса по получению категории: {}", id);
        CategoryDto category = categoryService.getCategoryById(id);
        log.info("Окончание обработки запроса по получению категории");
        return category;
    }
}
