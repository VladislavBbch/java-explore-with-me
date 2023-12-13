package ru.practicum.ewm.main.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.controller.Create;
import ru.practicum.ewm.main.controller.Update;
import ru.practicum.ewm.main.dto.CompilationRequestDto;
import ru.practicum.ewm.main.dto.CompilationResponseDto;
import ru.practicum.ewm.main.service.CompilationService;

import static ru.practicum.ewm.main.controller.Constant.ADMIN_URL_PREFIX;

@RestController
@RequestMapping(path = ADMIN_URL_PREFIX + "/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto createCompilation(@RequestBody @Validated({Create.class}) CompilationRequestDto compilationDto) {
        log.info("Начало обработки запроса на создание подборки событий: {}", compilationDto);
        CompilationResponseDto newCompilation = compilationService.createCompilation(compilationDto);
        log.info("Окончание обработки запроса на создание подборки событий");
        return newCompilation;
    }

    @PatchMapping("/{id}")
    public CompilationResponseDto updateCompilation(@PathVariable Long id,
                                                    @RequestBody @Validated({Update.class}) CompilationRequestDto compilationDto) {
        log.info("Начало обработки запроса на обновление подборки событий: {}", id);
        CompilationResponseDto existingCompilation = compilationService.updateCompilation(id, compilationDto);
        log.info("Окончание обработки запроса на обновление подборки событий");
        return existingCompilation;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long id) {
        log.info("Начало обработки запроса на удаление подборки событий: {}", id);
        compilationService.deleteCompilation(id);
        log.info("Окончание обработки запроса на удаление подборки событий");
    }
}
