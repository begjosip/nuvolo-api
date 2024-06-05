package com.nuvolo.nuvoloapi.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDto {

    private Long id;

    @NotBlank(message = "Category name must not be blank")
    private String name;

    private String description;
}
