package me.rkomarov.catalog.controller;

import lombok.RequiredArgsConstructor;
import me.rkomarov.catalog.controller.dto.ProductDetailsResponseDto;
import me.rkomarov.catalog.controller.dto.ProductRequestDto;
import me.rkomarov.catalog.controller.dto.ProductResponseDto;
import me.rkomarov.catalog.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController implements ProductResource {

    private final ProductService productService;

    @Override
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') || (hasRole('USER') && #showDeleted == false)")
    public List<ProductResponseDto> getAll(@RequestParam(defaultValue = "false") boolean showDeleted) {
        return productService.getAllProducts(showDeleted);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ProductDetailsResponseDto get(@PathVariable long id) {
        return productService.getProduct(id);
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDetailsResponseDto create(@RequestBody @Valid ProductRequestDto productRequestDto) {
        return productService.createProduct(productRequestDto);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void update(@PathVariable long id, @RequestBody @Valid ProductRequestDto productRequestDto) {
        productService.updateProduct(id, productRequestDto);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable long id) {
        productService.deleteProduct(id);
    }

    @Override
    @PutMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public void restore(@PathVariable long id) {
        productService.restoreProduct(id);
    }
}
