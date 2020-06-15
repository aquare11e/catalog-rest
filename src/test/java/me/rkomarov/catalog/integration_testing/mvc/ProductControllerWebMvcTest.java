package me.rkomarov.catalog.integration_testing.mvc;

import me.rkomarov.catalog.contoller.ProductController;
import me.rkomarov.catalog.contoller.dto.ProductDetailsResponseDto;
import me.rkomarov.catalog.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("mvc")
@WebMvcTest(value = ProductController.class)
public class ProductControllerWebMvcTest {

    @MockBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void getProductTest() throws Exception {
        // Given
        when(productService.getAllProducts(eq(false))).thenReturn(Collections.emptyList());

        // When
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/product"))
                .andReturn().getResponse();

        // Then
        Object[] responseBody = objectMapper.readValue(response.getContentAsByteArray(), Object[].class);

        assertEquals(200, response.getStatus());
        assertEquals(0, responseBody.length);
        verify(productService, only()).getAllProducts(eq(false));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void getSingleProductTest() throws Exception {
        // Given
        long productId = 1L;

        ProductDetailsResponseDto responseDto = new ProductDetailsResponseDto();
        responseDto.setTitle("title");
        responseDto.setPrice(BigDecimal.TEN);
        when(productService.getProduct(eq(productId))).thenReturn(responseDto);

        // When
        MockHttpServletResponse response = mockMvc
                .perform(MockMvcRequestBuilders.get("/product/" + productId))
                .andReturn().getResponse();

        // Then
        ProductDetailsResponseDto responseBody = objectMapper.readValue(
                response.getContentAsByteArray(), ProductDetailsResponseDto.class
        );

        assertEquals(200, response.getStatus());
        assertEquals(responseDto.getTitle(), responseBody.getTitle());
        assertEquals(responseDto.getPrice(), responseBody.getPrice());
        verify(productService, only()).getProduct(eq(productId));
    }
}

