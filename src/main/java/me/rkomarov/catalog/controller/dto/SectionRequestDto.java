package me.rkomarov.catalog.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class SectionRequestDto {
    @NotBlank
    private String title;
}
