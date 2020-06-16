package me.rkomarov.catalog.utils;

import ma.glasnost.orika.MapperFactory;
import me.rkomarov.catalog.controller.dto.ProductRequestDto;
import me.rkomarov.catalog.db.model.Product;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.stereotype.Component;

@Component
public class DtoToProductMapper implements OrikaMapperFactoryConfigurer {

    @Override
    public void configure(MapperFactory orikaMapperFactory) {
        orikaMapperFactory.classMap(ProductRequestDto.class, Product.class)
                .field("amount", "amount.amount")
                .field("unit", "amount.unit")
                .exclude("sectionId")
                .byDefault()
                .register();
    }
}
