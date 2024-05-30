package com.nuvolo.nuvoloapi.model.dto.response;

import com.nuvolo.nuvoloapi.model.entity.Discount;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class DiscountResponseDto {

    private Long id;

    private String name;

    private String description;

    private BigDecimal discountPercentage;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Boolean active;

    private LocalDateTime createdAt;

    public static DiscountResponseDto mapDiscountEntity(Discount discount) {
        return DiscountResponseDto.builder()
                .id(discount.getId())
                .name(discount.getName())
                .description(discount.getDescription())
                .discountPercentage(discount.getDiscountPercentage())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .active(discount.getActive())
                .createdAt(discount.getCreatedAt())
                .build();
    }
}
