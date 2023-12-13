package ru.practicum.ewm.main.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            ValidateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final Exception e) {
        log.error("Ошибка при валидации {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка при валидации", e.getMessage());
    }

    @ExceptionHandler({ObjectNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        log.error("Объект не найден {}", e.getMessage(), e);
        return new ErrorResponse("Объект не найден", e.getMessage());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        log.error("Ошибка {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка " + e.getName() + ": " + e.getValue(), e.getMessage());
    }

    @ExceptionHandler({DataIntegrityViolationException.class, ConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(final RuntimeException e) {
        log.error("Нарушение целостности данных {}", e.getMessage(), e);
        return new ErrorResponse("Нарушение целостности данных", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Throwable e) {
        log.error("Ошибка {}", e.getMessage(), e);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        printWriter.flush();
        return new ErrorResponse(e.getMessage(), stringWriter.toString());
    }
}