package com.nuvolo.nuvoloapi.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryRequestDto {

    private Long id;

    @NotBlank(message = "Category name must not be blank")
    private String name;

    private String description;
}
