package me.rkomarov.catalog.contoller;

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
import me.rkomarov.catalog.contoller.dto.ExceptionResponseDto;
import me.rkomarov.catalog.contoller.dto.SectionDetailsResponseDto;
import me.rkomarov.catalog.contoller.dto.SectionRequestDto;
import me.rkomarov.catalog.contoller.dto.SectionResponseDto;

import java.util.List;
import java.util.Set;

/**
 * Section resource interface with OpenAPI annotations
 */
@Tag(name = "section", description = "Section API")
public interface SectionResource {

    @Operation(description = "Get all sections", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SectionResponseDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User with role 'USER' cannot to receive deleted entities (using showDeleted = true)"
            )
    })
    List<SectionResponseDto> getAll(
            @Parameter(description="If 'true' then return both sections: deleted and active") boolean showDeleted
    );

    @Operation(description = "Get section tree", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SectionDetailsResponseDto.class)
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
    SectionDetailsResponseDto getTree(@Parameter(description="Section ID", required = true) long id);

    @Operation(description = "Get full section's tree", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SectionDetailsResponseDto.class))
                    )
            )
    })
    List<SectionDetailsResponseDto> getAllTree();

    @Operation(description = "Get section", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SectionResponseDto.class)
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
    SectionResponseDto get(@Parameter(description="Section ID", required = true) long id);

    @Operation(description = "Create section", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Successful create operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SectionDetailsResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Incorrect request body parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseDto.class)
                    )
            )
    })
    SectionDetailsResponseDto create(
            @RequestBody(description = "Section DTO", required = true) SectionRequestDto sectionDto
    );

    @Operation(description = "Update section", security = @SecurityRequirement(name = "basicAuth"))
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
                    description = "Section not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseDto.class)
                    )
            )
    })
    void update(@Parameter(description="Section ID", required = true) long id,
                @RequestBody (description = "Section DTO", required = true) SectionRequestDto sectionDto);

    @Operation(description = "Soft delete section and child elements", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation"
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
    void delete(@Parameter(description="Section ID", required = true) long id);

    @Operation(description = "Update subsection set", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Attempt to create circular section",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Section or subsection(s) not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionResponseDto.class)
                    )
            )
    })
    void updateSubsections(@Parameter(description="Section ID", required = true) long id,
                           @RequestBody(description = "Subsections ID set", required = true) Set<Long> subsectionIds);

    @Operation(description = "Restore section and child elements", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Attempt to restore section with deleted parent",
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
    void restore(@Parameter(description="Section ID", required = true) long id);
}
