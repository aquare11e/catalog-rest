package me.rkomarov.catalog.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.rkomarov.catalog.db.model.AmountUnit;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProductDetailsResponseDto {
    private long id;
    private String title;
    private BigDecimal amount;
    private AmountUnit unit;
    private BigDecimal price;
    private boolean deleted;
    private SectionInfo section;

    @Data
    @NoArgsConstructor
    public static class SectionInfo {
        private long id;
        private String title;
    }
}
