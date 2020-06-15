package me.rkomarov.catalog.contoller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SectionResponseDto {
    private long id;
    private String title;
    private boolean deleted;
}
