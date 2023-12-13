package ru.practicum.ewm.main.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.UserDto;
import ru.practicum.ewm.main.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Arrays;
import java.util.List;

import static ru.practicum.ewm.main.controller.Constant.ADMIN_URL_PREFIX;

@RestController
@RequestMapping(path = ADMIN_URL_PREFIX + "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Начало обработки запроса на создание пользователя: {}", userDto);
        UserDto newUser = userService.createUser(userDto);
        log.info("Окончание обработки запроса на создание пользователя");
        return newUser;
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(defaultValue = "") Long[] ids,
                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                  @RequestParam(defaultValue = "10") @Min(1) @Max(100000) Integer size) {
        log.info("Начало обработки запроса по получению пользователей: {}, from={}, size={}",
                ids.length > 0 ? Arrays.toString(ids) : "null", from, size);
        List<UserDto> users = userService.getUsers(List.of(ids), from, size);
        log.info("Окончание обработки запроса по получению пользователей");
        return users;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        log.info("Начало обработки запроса на удаление пользователя: {}", id);
        userService.deleteUser(id);
        log.info("Окончание обработки запроса на удаление пользователя");
    }
}
