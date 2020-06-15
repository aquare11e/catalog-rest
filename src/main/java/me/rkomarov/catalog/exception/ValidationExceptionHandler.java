package me.rkomarov.catalog.exception;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import me.rkomarov.catalog.contoller.dto.ExceptionResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice(annotations = RestController.class)
@Hidden
public class ValidationExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ExceptionResponseDto handleValidationErrors(MethodArgumentNotValidException exception) {
        log.error("Validation exception was handled");

        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        if (fieldErrors.isEmpty()) {
            log.warn("Validation failure: unexpected error - empty list from MethodArgumentNotValidException");
            return new ExceptionResponseDto("Received object has errors");
        }

        List<String> errorStringList = fieldErrors.stream()
                .map(e -> String.format("%s %s", e.getField(), e.getDefaultMessage()))
                .collect(Collectors.toList());

        return new ExceptionResponseDto(errorStringList);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ExceptionResponseDto handleMessageNotReadable() {
        log.error("Wrong formatted data exception was handled");
        return new ExceptionResponseDto("Received message with incorrect format");
    }
}