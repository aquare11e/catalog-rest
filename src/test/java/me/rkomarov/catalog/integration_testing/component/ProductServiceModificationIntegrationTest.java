package me.rkomarov.catalog.integration_testing.component;

import me.rkomarov.catalog.controller.dto.ProductDetailsResponseDto;
import me.rkomarov.catalog.controller.dto.ProductRequestDto;
import me.rkomarov.catalog.db.ProductRepository;
import me.rkomarov.catalog.db.SectionRepository;
import me.rkomarov.catalog.db.model.AmountUnit;
import me.rkomarov.catalog.db.model.Product;
import me.rkomarov.catalog.db.model.Section;
import me.rkomarov.catalog.service.ProductService;
import me.rkomarov.catalog.testutils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("integration-testing")
public class ProductServiceModificationIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestUtils testUtils;

    @AfterEach
    public void clearTables() {
        testUtils.clearTables();
    }

    @Test
    @Transactional
    public void creationTest() {
        // Given
        Section section = sectionRepository.save(new Section().setTitle("Title"));
        ProductRequestDto productRequestDto = new ProductRequestDto()
                .setTitle("ProductTitle")
                .setSectionId(section.getId())
                .setAmount(BigDecimal.TEN)
                .setUnit(AmountUnit.KILOGRAM)
                .setPrice(BigDecimal.ZERO);

        // When
        ProductDetailsResponseDto productResponse = productService.createProduct(productRequestDto);
        entityManager.flush();

        // Then
        Optional<Product> optionalProduct = productRepository.findById(productResponse.getId());
        assertTrue(optionalProduct.isPresent());

        Product product = optionalProduct.get();
        assertEquals(productRequestDto.getTitle(), product.getTitle());
        assertEquals(productRequestDto.getSectionId(), product.getSection().getId());
        assertEquals(productRequestDto.getAmount(), product.getAmount().getAmount());
        assertEquals(productRequestDto.getUnit(), product.getAmount().getUnit());
        assertEquals(productRequestDto.getPrice(), product.getPrice());
    }

    @Test
    @Transactional
    public void updateTest() {
        // Given
        Section section = sectionRepository.save(new Section().setTitle("Title"));
        Product oldProduct = productRepository.save(new Product().setTitle("OldTitle").setSection(section));
        ProductRequestDto productRequestDto = new ProductRequestDto()
                .setTitle("NewTitle")
                .setSectionId(section.getId())
                .setAmount(BigDecimal.TEN)
                .setUnit(AmountUnit.KILOGRAM)
                .setPrice(BigDecimal.ZERO);

        // When
        productService.updateProduct(oldProduct.getId(), productRequestDto);
        entityManager.flush();

        // Then
        Optional<Product> optionalProduct = productRepository.findById(oldProduct.getId());
        assertTrue(optionalProduct.isPresent());

        Product product = optionalProduct.get();
        assertEquals(productRequestDto.getTitle(), product.getTitle());
        assertEquals(productRequestDto.getSectionId(), product.getSection().getId());
        assertEquals(productRequestDto.getAmount(), product.getAmount().getAmount());
        assertEquals(productRequestDto.getUnit(), product.getAmount().getUnit());
        assertEquals(productRequestDto.getPrice(), product.getPrice());
    }

    @Test
    @Transactional
    public void deleteTest() {
        // Given
        Section section = sectionRepository.save(new Section().setTitle("Title"));
        Product product = productRepository.save(new Product().setTitle("ProductTitle").setSection(section));

        // When
        productService.deleteProduct(product.getId());
        testUtils.flushClear();

        // Then
        Optional<Product> optionalProduct = productRepository.findById(product.getId());
        assertFalse(optionalProduct.isPresent());

        Product productFromNativeQuery = ((Product) entityManager
                .createNativeQuery("SELECT * FROM product WHERE id = " + product.getId(), Product.class)
                .getSingleResult());
        assertNotNull(productFromNativeQuery);
        assertEquals(product.getTitle(), productFromNativeQuery.getTitle());
        assertTrue(productFromNativeQuery.isDeleted());
    }

    @Test
    @Transactional
    public void restoreTest() {
        // Given
        Section section = sectionRepository.save(new Section().setTitle("Title"));
        Product product = productRepository.save(new Product().setTitle("ProductTitle").setSection(section).setDeleted(true));

        testUtils.flushClear();

        Optional<Product> emptyOptionalProduct = productRepository.findById(product.getId());
        assertFalse(emptyOptionalProduct.isPresent());

        // When
        productService.restoreProduct(product.getId());
        testUtils.flushClear();

        // Then
        Optional<Product> presentOptionalProduct = productRepository.findById(product.getId());
        assertTrue(presentOptionalProduct.isPresent());
        assertEquals(product.getTitle(), presentOptionalProduct.get().getTitle());
        assertFalse(presentOptionalProduct.get().isDeleted());
    }
}
