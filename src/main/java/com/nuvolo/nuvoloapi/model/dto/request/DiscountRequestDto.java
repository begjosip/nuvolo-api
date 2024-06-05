package com.nuvolo.nuvoloapi.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class DiscountRequestDto {

    private Long id;

    private String name;

    private String description;

    @NotNull(message = "Discount percentage must not be empty")
    private BigDecimal discountPercentage;

    @NotNull(message = "Set valid discount start date")
    private LocalDateTime startDate;

    @NotNull(message = "Set valid discount end date")
    private LocalDateTime endDate;

    private Boolean active;

}
