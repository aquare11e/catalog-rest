package me.rkomarov.catalog.utils;

import ma.glasnost.orika.MapperFactory;
import me.rkomarov.catalog.controller.dto.ProductDetailsResponseDto;
import me.rkomarov.catalog.controller.dto.ProductResponseDto;
import me.rkomarov.catalog.db.model.Product;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.stereotype.Component;

@Component
public class ProductToDtoMapper implements OrikaMapperFactoryConfigurer {

    @Override
    public void configure(MapperFactory orikaMapperFactory) {
        orikaMapperFactory.classMap(Product.class, ProductResponseDto.class)
                .field("amount.amount", "amount")
                .field("amount.unit", "unit")
                .byDefault()
                .register();

        orikaMapperFactory.classMap(Product.class, ProductDetailsResponseDto.class)
                .field("amount.amount", "amount")
                .field("amount.unit", "unit")
                .byDefault()
                .register();
    }
}
