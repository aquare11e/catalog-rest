package me.rkomarov.catalog.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import me.rkomarov.catalog.controller.dto.ProductDetailsResponseDto;
import me.rkomarov.catalog.controller.dto.ProductRequestDto;
import me.rkomarov.catalog.controller.dto.ProductResponseDto;
import me.rkomarov.catalog.db.ProductRepository;
import me.rkomarov.catalog.db.SectionRepository;
import me.rkomarov.catalog.db.model.Product;
import me.rkomarov.catalog.db.model.Section;
import me.rkomarov.catalog.exception.BusinessLogicException;
import me.rkomarov.catalog.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SectionRepository sectionRepository;
    private final MapperFacade mapperFacade;

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts(boolean showDeleted) {
        List<Product> products = showDeleted ?
                productRepository.fetchAllProductsWithDeleted() :
                productRepository.findAll();

        return mapperFacade.mapAsList(products, ProductResponseDto.class);
    }

    @Transactional(readOnly = true)
    public ProductDetailsResponseDto getProduct(long productId) {
        Product product = productRepository.fetchWithSectionById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id was not found: " + productId));
        return mapperFacade.map(product, ProductDetailsResponseDto.class);
    }

    @Transactional
    public ProductDetailsResponseDto createProduct(ProductRequestDto productRequestDto) {
        Product product = mapperFacade.map(productRequestDto, Product.class);
        product.setSection(getSectionOrThrow(productRequestDto.getSectionId()));

        Product savedProduct = productRepository.save(product);
        return mapperFacade.map(savedProduct, ProductDetailsResponseDto.class);
    }

    @Transactional
    public void updateProduct(long productId, ProductRequestDto productRequestDto) {
        Product product = getProductOrThrow(productId);
        mapperFacade.map(productRequestDto, product);
        product.setSection(getSectionOrThrow(productRequestDto.getSectionId()));

        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(long productId) {
        Product product = getProductOrThrow(productId);
        productRepository.softDelete(product);
    }

    @Transactional
    public void restoreProduct(long productId) {
        Product product = productRepository.fetchProduct(productId)
                .orElseThrow(() -> new NotFoundException("Product with id was not found: " + productId));

        if (productRepository.existDeletedSectionByProductId(productId) > 0) {
            throw new BusinessLogicException("Impossible to restore product with deleted section");
        }

        productRepository.restoreDeleted(product);
    }

    private Section getSectionOrThrow(long sectionId) {
        return sectionRepository.findById(sectionId)
                .orElseThrow(() -> new NotFoundException("Section with id was not found: " + sectionId));
    }

    private Product getProductOrThrow(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.orElseThrow(() -> new NotFoundException("Product with id was not found: " + id));
    }
}
