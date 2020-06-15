package me.rkomarov.catalog.db.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Amount {

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "unit")
    @Enumerated(EnumType.STRING)
    private AmountUnit unit;
}
