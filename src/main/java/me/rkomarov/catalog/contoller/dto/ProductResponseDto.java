package me.rkomarov.catalog.contoller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.rkomarov.catalog.db.model.AmountUnit;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProductResponseDto {
    private long id;
    private String title;
    private BigDecimal amount;
    private AmountUnit unit;
    private BigDecimal price;
    private boolean deleted;
}
