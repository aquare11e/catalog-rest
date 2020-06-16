package me.rkomarov.catalog.controller.dto;

import lombok.Value;

import java.util.List;

@Value
public class ExceptionResponseDto {
    List<String> messages;

    public ExceptionResponseDto(String message) {
        this.messages = List.of(message);
    }

    public ExceptionResponseDto(List<String> messages) {
        this.messages = messages;
    }
}
