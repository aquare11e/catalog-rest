package me.rkomarov.catalog.exception;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import me.rkomarov.catalog.contoller.dto.ExceptionResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Hidden
public class BusinessLogicExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessLogicException.class)
    public ExceptionResponseDto handleBLE(BusinessLogicException businessLogicException) {
        log.error("Handle BusinessLogicException with message: {}", businessLogicException.getMessage());
        return new ExceptionResponseDto(businessLogicException.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ExceptionResponseDto handleNFE(NotFoundException notFoundException) {
        log.error("Handle NotFoundException with message: {}", notFoundException.getMessage());
        return new ExceptionResponseDto(notFoundException.getMessage());
    }
}
