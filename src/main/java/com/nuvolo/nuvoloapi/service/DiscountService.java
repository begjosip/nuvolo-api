package com.nuvolo.nuvoloapi.service;

import com.nuvolo.nuvoloapi.exceptions.InvalidDiscountException;
import com.nuvolo.nuvoloapi.model.dto.request.DiscountRequestDto;
import com.nuvolo.nuvoloapi.model.dto.response.DiscountResponseDto;
import com.nuvolo.nuvoloapi.model.entity.Discount;
import com.nuvolo.nuvoloapi.repository.DiscountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountService {

    private final DiscountRepository discountRepository;

    @Transactional
    public List<DiscountResponseDto> getAllDiscounts() {
        log.debug("Fetching all discounts from database.");
        List<DiscountResponseDto> discounts = discountRepository.findAll().stream().map(DiscountResponseDto::mapDiscountEntity).toList();
        if (discounts.isEmpty()) {
            log.debug("No discounts found. Returning empty list.");
            return Collections.emptyList();
        }
        log.debug("Returning list of all discounts.");
        return discounts;
    }


    @Transactional
    public void createDiscount(DiscountRequestDto discountRequest) {
        this.validateDiscount(discountRequest);
        Discount discount = Discount.builder()
                .name(discountRequest.getName())
                .description(discountRequest.getDescription())
                .discountPercentage(discountRequest.getDiscountPercentage().setScale(2, RoundingMode.HALF_UP))
                .startDate(discountRequest.getStartDate())
                .endDate(discountRequest.getEndDate())
                .active(Boolean.TRUE)
                .build();

        log.debug("Saving discount entity to database.");
        Discount savedDiscount = discountRepository.save(discount);
        log.debug("Discount with ID:{} saved to database.", savedDiscount.getId());
    }

    private void validateDiscount(DiscountRequestDto discountRequest) {
        if (discountRequest.getStartDate().isBefore(LocalDateTime.now())) {
            throw new InvalidDiscountException("Discount start date needs to be after current date.");
        }
        if (discountRequest.getEndDate().isBefore(discountRequest.getStartDate())) {
            throw new InvalidDiscountException("Discount end date needs to be after start date.");
        }
        BigDecimal roundedDiscount = discountRequest.getDiscountPercentage().setScale(2, RoundingMode.HALF_UP);
        if (!(roundedDiscount.compareTo(BigDecimal.ZERO) > 0 && roundedDiscount.compareTo(BigDecimal.ONE) < 0)) {
            throw new InvalidDiscountException("Invalid discount percentage. Percentage needs to be between 0.00 and 1.00 both excluded.");
        }
    }
}
