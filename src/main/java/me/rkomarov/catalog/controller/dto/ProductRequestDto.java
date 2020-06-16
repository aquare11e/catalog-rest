package me.rkomarov.catalog.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import me.rkomarov.catalog.db.model.AmountUnit;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ProductRequestDto {

    @NotBlank
    private String title;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private AmountUnit unit;

    @NotNull
    @PositiveOrZero
    private BigDecimal price;

    @NotNull
    @Min(1)
    private Long sectionId;
}
