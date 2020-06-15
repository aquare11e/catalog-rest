package me.rkomarov.catalog.service;

import ma.glasnost.orika.MapperFacade;
import me.rkomarov.catalog.contoller.dto.ProductDetailsResponseDto;
import me.rkomarov.catalog.contoller.dto.ProductRequestDto;
import me.rkomarov.catalog.db.ProductRepository;
import me.rkomarov.catalog.db.SectionRepository;
import me.rkomarov.catalog.db.model.Product;
import me.rkomarov.catalog.db.model.Section;
import me.rkomarov.catalog.exception.BusinessLogicException;
import me.rkomarov.catalog.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private ProductService productService;

    @Test
    public void createProductWithExistedSection() {
        // Given
        long sectionId = 1L;
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setSectionId(sectionId);

        Section fetchedSection = new Section();
        Product createdProduct = new Product();
        ProductDetailsResponseDto expectedResult = new ProductDetailsResponseDto();

        when(sectionRepository.findById(eq(sectionId))).thenReturn(Optional.of(fetchedSection));
        when(mapperFacade.map(eq(productRequestDto), eq(Product.class))).thenReturn(createdProduct);
        when(productRepository.save(eq(createdProduct))).thenReturn(createdProduct);
        when(mapperFacade.map(eq(createdProduct), eq(ProductDetailsResponseDto.class))).thenReturn(expectedResult);

        // When
        ProductDetailsResponseDto result = productService.createProduct(productRequestDto);

        // Then
        verify(sectionRepository, only()).findById(eq(sectionId));
        verify(mapperFacade, times(1)).map(eq(productRequestDto), eq(Product.class));
        verify(productRepository, only()).save(createdProduct);
        verify(mapperFacade, times(1)).map(eq(createdProduct), eq(ProductDetailsResponseDto.class));

        assertEquals(expectedResult, result);
        assertEquals(createdProduct.getSection(), fetchedSection);
    }

    @Test
    public void createProductWithNotFoundSection() {
        // Given
        long sectionId = 1L;
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setSectionId(sectionId);

        when(sectionRepository.findById(eq(sectionId))).thenReturn(Optional.empty());

        // Expect
        assertThrows(NotFoundException.class, () -> productService.createProduct(productRequestDto));
        verify(sectionRepository, only()).findById(eq(sectionId));
    }

    @Test
    public void updateProductWithExistedNewSection() {
        // Given
        long productId = 1L;
        long sectionId = 2L;
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setSectionId(sectionId);

        Section fetchedSection = new Section();
        Product fetchedProduct = new Product();

        when(productRepository.findById(eq(productId))).thenReturn(Optional.of(fetchedProduct));
        when(sectionRepository.findById(eq(sectionId))).thenReturn(Optional.of(fetchedSection));

        // When
        productService.updateProduct(productId, productRequestDto);

        // Then
        verify(productRepository, times(1)).findById(eq(productId));
        verify(sectionRepository, only()).findById(eq(sectionId));
        verify(mapperFacade, only()).map(eq(productRequestDto), eq(fetchedProduct));
        verify(productRepository, times(1)).save(eq(fetchedProduct));

        assertEquals(fetchedSection, fetchedProduct.getSection());
    }

    @Test
    public void updateProductWithNotFoundOldProduct() {
        // Given
        long productId = 1L;
        ProductRequestDto productRequestDto = new ProductRequestDto();

        when(productRepository.findById(eq(productId))).thenReturn(Optional.empty());

        // Expect
        assertThrows(NotFoundException.class, () -> productService.updateProduct(productId, productRequestDto));
        verify(productRepository, only()).findById(eq(productId));
    }

    @Test
    public void updateProductWithNotFoundSection() {
        // Given
        long productId = 1L;
        long sectionId = 2L;
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setSectionId(sectionId);

        when(productRepository.findById(eq(productId))).thenReturn(Optional.of(new Product()));
        when(sectionRepository.findById(eq(sectionId))).thenReturn(Optional.empty());

        // Expect
        assertThrows(NotFoundException.class, () -> productService.updateProduct(productId, productRequestDto));
        verify(productRepository, only()).findById(eq(productId));
        verify(sectionRepository, only()).findById(eq(sectionId));
    }

    @Test
    public void deleteExistedProduct() {
        // Given
        long productId = 1L;
        Product fetchedProduct = new Product();

        when(productRepository.findById(eq(productId))).thenReturn(Optional.of(fetchedProduct));

        // When
        productService.deleteProduct(productId);

        // Then
        verify(productRepository, times(1)).findById(eq(productId));
        verify(productRepository, times(1)).softDelete(eq(fetchedProduct));
    }

    @Test
    public void deleteNotFoundProduct() {
        // Given
        long productId = 1L;
        when(productRepository.findById(eq(productId))).thenReturn(Optional.empty());

        // Expect
        assertThrows(NotFoundException.class, () -> productService.deleteProduct(productId));
        verify(productRepository, only()).findById(eq(productId));
    }

    @Test
    public void restoreDeletedProductWithActiveSection() {
        // Given
        long productId = 1L;
        Product fetchedProduct = new Product();

        when(productRepository.fetchProduct(eq(productId))).thenReturn(Optional.of(fetchedProduct));
        when(productRepository.existDeletedSectionByProductId(eq(productId))).thenReturn(0);

        // When
        productService.restoreProduct(productId);

        // Then
        verify(productRepository, times(1)).fetchProduct(eq(productId));
        verify(productRepository, times(1)).existDeletedSectionByProductId(eq(productId));
        verify(productRepository, times(1)).restoreDeleted(eq(fetchedProduct));
    }

    @Test
    public void restoreNotFoundProduct() {
        // Given
        long productId = 1L;
        when(productRepository.fetchProduct(eq(productId))).thenReturn(Optional.empty());

        // Expect
        assertThrows(NotFoundException.class, () -> productService.restoreProduct(productId));
        verify(productRepository, only()).fetchProduct(eq(productId));
    }

    @Test
    public void restoreDeletedProductWithDeletedSection() {
        // Given
        long productId = 1L;
        Product fetchedProduct = new Product();

        when(productRepository.fetchProduct(eq(productId))).thenReturn(Optional.of(fetchedProduct));
        when(productRepository.existDeletedSectionByProductId(eq(productId))).thenReturn(1);

        // Expect
        assertThrows(BusinessLogicException.class, () -> productService.restoreProduct(productId));
        verify(productRepository, times(1)).fetchProduct(eq(productId));
        verify(productRepository, times(1)).existDeletedSectionByProductId(eq(productId));
        verifyNoMoreInteractions(productRepository);
    }
}