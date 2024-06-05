package com.nuvolo.nuvoloapi.model.dto.response;

import com.nuvolo.nuvoloapi.model.entity.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductResponseDto {

    private Long id;

    private String name;

    private String description;

    private String typeName;

    private String categoryName;

    private BigDecimal price;

    private DiscountResponseDto discount;

    private List<String> imagesUrls;

    public static ProductResponseDto mapProductEntity(Product product, List<String> imagesUrls) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .typeName(product.getType().getName().toString())
                .categoryName(product.getCategory().getName())
                .price(product.getPrice())
                .discount(product.getDiscount() == null ? null : DiscountResponseDto.mapDiscountEntity(product.getDiscount()))
                .imagesUrls(imagesUrls)
                .build();
    }
}
