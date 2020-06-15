package me.rkomarov.catalog.integration_testing.component;

import me.rkomarov.catalog.contoller.dto.ProductDetailsResponseDto;
import me.rkomarov.catalog.contoller.dto.ProductResponseDto;
import me.rkomarov.catalog.db.ProductRepository;
import me.rkomarov.catalog.db.SectionRepository;
import me.rkomarov.catalog.db.model.Amount;
import me.rkomarov.catalog.db.model.AmountUnit;
import me.rkomarov.catalog.db.model.Product;
import me.rkomarov.catalog.db.model.Section;
import me.rkomarov.catalog.exception.NotFoundException;
import me.rkomarov.catalog.service.ProductService;
import me.rkomarov.catalog.testutils.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("integration-testing")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductServiceReadIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private TestUtils testUtils;

    private Product activeProduct;
    private Product deletedProduct;

    @BeforeAll
    @Transactional
    public void initProducts() {
        Section section = new Section().setTitle("Section");
        Section savedSection = sectionRepository.save(section);

        Product product1 = new Product()
                .setSection(savedSection)
                .setTitle("1")
                .setDeleted(false)
                .setAmount(new Amount(BigDecimal.ONE, AmountUnit.PIECE))
                .setPrice(BigDecimal.ONE);

        Product product2 = new Product()
                .setSection(savedSection)
                .setTitle("2")
                .setDeleted(false)
                .setAmount(new Amount(BigDecimal.ONE, AmountUnit.PIECE))
                .setPrice(BigDecimal.ONE);

        Product product3 = new Product()
                .setSection(savedSection)
                .setTitle("3")
                .setDeleted(true)
                .setAmount(new Amount(BigDecimal.ONE, AmountUnit.PIECE))
                .setPrice(BigDecimal.ONE);

        activeProduct = productRepository.save(product1);
        productRepository.save(product2);
        deletedProduct = productRepository.save(product3);
    }

    @AfterAll
    public void clearTables() {
        testUtils.clearTables();
    }

    @Test
    @Transactional(readOnly = true)
    public void getAllActiveProductsTest() {
        List<ProductResponseDto> activeProducts = productService.getAllProducts(false);

        assertEquals(2, activeProducts.size());
        activeProducts.forEach(product -> assertTrue(Set.of("1", "2").contains(product.getTitle())));
    }

    @Test
    @Transactional(readOnly = true)
    public void getAllProductsTest() {
        List<ProductResponseDto> activeProducts = productService.getAllProducts(true);

        assertEquals(3, activeProducts.size());
        activeProducts.forEach(product -> assertTrue(Set.of("1", "2", "3").contains(product.getTitle())));
    }

    @Test
    @Transactional(readOnly = true)
    public void getActiveProductTest() {
        ProductDetailsResponseDto productDto = productService.getProduct(activeProduct.getId());
        assertEquals(activeProduct.getTitle(), productDto.getTitle());
        assertEquals(activeProduct.getSection().getId(), productDto.getSection().getId());
    }

    @Test
    @Transactional(readOnly = true)
    public void getDeletedProductTest() {
        assertThrows(NotFoundException.class, () -> productService.getProduct(deletedProduct.getId()));
    }
}

