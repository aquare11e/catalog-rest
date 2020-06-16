package me.rkomarov.catalog.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class SectionDetailsResponseDto {
    private long id;
    private String title;
    private boolean deleted;
    private Set<SectionDetailsResponseDto> subsections;
    private Set<ProductResponseDto> products;
}
