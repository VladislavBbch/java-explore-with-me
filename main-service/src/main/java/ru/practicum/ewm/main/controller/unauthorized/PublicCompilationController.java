package ru.practicum.ewm.main.controller.unauthorized;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.CompilationResponseDto;
import ru.practicum.ewm.main.service.CompilationService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationResponseDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                        @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                        @RequestParam(defaultValue = "10") @Min(1) @Max(1000127) Integer size) {
        log.info("Начало обработки запроса по получению подборки событий");
        List<CompilationResponseDto> compilations = compilationService.getCompilations(pinned, from, size);
        log.info("Окончание обработки запроса по получению подборки событий");
        return compilations;
    }

    @GetMapping("/{id}")
    public CompilationResponseDto getCompilationById(@PathVariable Long id) {
        log.info("Начало обработки запроса по получению подборки событий: {}", id);
        CompilationResponseDto compilation = compilationService.getCompilationById(id);
        log.info("Окончание обработки запроса по получению подборки событий");
        return compilation;
    }
}
