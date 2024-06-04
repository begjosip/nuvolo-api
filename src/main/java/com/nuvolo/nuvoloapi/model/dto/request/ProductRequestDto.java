package com.nuvolo.nuvoloapi.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequestDto {

    private Long id;

    @NotNull(message = "Product type ID must be given")
    private Long typeId;

    @NotNull(message = "Product category ID  must be given")
    private Long categoryId;

    @NotBlank(message = "Insert valid product name")
    private String name;

    @NotBlank(message = "Insert valid product description")
    private String description;

    @NotNull(message = "Insert valid product price")
    private BigDecimal price;

    @NotNull(message = "Insert product quantity in stock")
    private Integer quantity;

}
