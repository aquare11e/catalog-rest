package me.rkomarov.catalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.rkomarov.catalog.controller.dto.ExceptionResponseDto;
import me.rkomarov.catalog.controller.dto.ProductDetailsResponseDto;
import me.rkomarov.catalog.controller.dto.ProductRequestDto;
import me.rkomarov.catalog.controller.dto.ProductResponseDto;

import java.util.List;

/**
 * Product resource interface with OpenAPI annotations
 */
@Tag(name = "product", description = "Product API")
public interface ProductResource {

    @Operation(description = "Get all products", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductResponseDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User with role 'USER' cannot to receive deleted entities (using showDeleted = true)"
            )
    })
    List<ProductResponseDto> getAll(@Parameter(description="If 'true' then return both sections: deleted and active") boolean showDeleted);

    @Operation(description = "Get product", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductDetailsResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseDto.class)
                    )
            )
    })
    ProductDetailsResponseDto get(@Parameter(description="Product ID", required = true) long id);

    @Operation(description = "Create product", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Successful create operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductDetailsResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Incorrect request body parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Section not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseDto.class)
                    )
            )
    })
    ProductDetailsResponseDto create(
            @RequestBody(description = "Product DTO", required = true) ProductRequestDto productRequestDto
    );

    @Operation(description = "Update product", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Incorrect request body parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product or section not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseDto.class)
                    )
            )
    })
    void update(@Parameter(description="Product ID", required = true) long id,
                @RequestBody(description = "Product DTO", required = true) ProductRequestDto productRequestDto);

    @Operation(description = "Update product", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseDto.class)
                    )
            )
    })
    void delete(@Parameter(description="Product ID", required = true) long id);

    @Operation(description = "Restore deleted product", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Attempt to restore product with deleted section",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseDto.class)
                    )
            )
    })
    void restore(@Parameter(description="Product ID", required = true) long id);
}
