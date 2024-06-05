package com.nuvolo.nuvoloapi.service;

import com.nuvolo.nuvoloapi.exceptions.InvalidDiscountException;
import com.nuvolo.nuvoloapi.model.dto.request.DiscountRequestDto;
import com.nuvolo.nuvoloapi.model.dto.response.DiscountResponseDto;
import com.nuvolo.nuvoloapi.model.entity.Discount;
import com.nuvolo.nuvoloapi.repository.DiscountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private DiscountService discountService;

    @Captor
    private ArgumentCaptor<Discount> discountCaptor;

    @Test
    void testCreateDiscount_success() {
        DiscountRequestDto validRequest = this.createValidDiscountRequestDto();
        when(discountRepository.save(discountCaptor.capture())).thenAnswer(invocation -> {
            Discount capturedDiscount = discountCaptor.getValue();
            capturedDiscount.setId(1L);
            return capturedDiscount;
        });
        assertDoesNotThrow(() -> discountService.createDiscount(validRequest));
        verify(discountRepository, times(1)).save(any(Discount.class));
        Discount capturedDiscount = discountCaptor.getValue();
        assertEquals(1L, capturedDiscount.getId());
        assertEquals(validRequest.getName(), capturedDiscount.getName());
        assertEquals(validRequest.getDescription(), capturedDiscount.getDescription());
        assertEquals(validRequest.getDiscountPercentage().setScale(2, RoundingMode.HALF_UP), capturedDiscount.getDiscountPercentage());
        assertEquals(validRequest.getStartDate(), capturedDiscount.getStartDate());
        assertEquals(validRequest.getEndDate(), capturedDiscount.getEndDate());
    }

    @Test
    void testCreateDiscount_invalidStartDate() {
        DiscountRequestDto invalidDiscountRequest = DiscountRequestDto.builder()
                .name("Test Discount")
                .description("Discount Description")
                .discountPercentage(new BigDecimal("0.5"))
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();
        InvalidDiscountException exception = assertThrows(InvalidDiscountException.class, () -> discountService.createDiscount(invalidDiscountRequest));
        assertEquals("Discount start date needs to be after current date.", exception.getMessage());
        verify(discountRepository, never()).save(any(Discount.class));
    }

    @Test
    void testCreateDiscount_invalidEndDate() {
        DiscountRequestDto invalidDiscountRequest = DiscountRequestDto.builder()
                .name("Test Discount")
                .description("Discount Description")
                .discountPercentage(new BigDecimal("0.5"))
                .startDate(LocalDateTime.now().plusDays(2))
                .endDate(LocalDateTime.now().plusDays(1))
                .build();
        InvalidDiscountException exception = assertThrows(InvalidDiscountException.class, () -> discountService.createDiscount(invalidDiscountRequest));
        assertEquals("Discount end date needs to be after start date.", exception.getMessage());
        verify(discountRepository, never()).save(any(Discount.class));
    }

    @Test
    void testCreateDiscount_invalidDiscountPercentageZero() {
        DiscountRequestDto invalidDiscountRequest = DiscountRequestDto.builder()
                .name("Test Discount")
                .description("Discount Description")
                .discountPercentage(new BigDecimal("0.0001"))
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();
        InvalidDiscountException exception = assertThrows(InvalidDiscountException.class, () -> discountService.createDiscount(invalidDiscountRequest));
        assertEquals("Invalid discount percentage. Percentage needs to be between 0.00 and 1.00 both excluded.", exception.getMessage());
        verify(discountRepository, never()).save(any(Discount.class));
    }

    @Test
    void testCreateDiscount_invalidDiscountPercentageOneOrMore() {
        DiscountRequestDto invalidDiscountRequest = DiscountRequestDto.builder()
                .name("Test Discount")
                .description("Discount Description")
                .discountPercentage(new BigDecimal("0.999"))
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();
        InvalidDiscountException exception = assertThrows(InvalidDiscountException.class, () -> discountService.createDiscount(invalidDiscountRequest));
        assertEquals("Invalid discount percentage. Percentage needs to be between 0.00 and 1.00 both excluded.", exception.getMessage());
        verify(discountRepository, never()).save(any(Discount.class));
    }

    @Test
    void testGetAllDiscounts() {
        Discount discount = Discount.builder()
                .id(1L)
                .name("Test Discount")
                .description("Discount Description")
                .discountPercentage(new BigDecimal("0.999"))
                .startDate(LocalDateTime.now().plusDays(2))
                .endDate(LocalDateTime.now().plusDays(1))
                .build();
        List<Discount> discounts = Collections.singletonList(discount);
        when(discountRepository.findAll()).thenReturn(discounts);
        List<DiscountResponseDto> responseDtoList = discountService.getAllDiscounts();
        assertEquals(1, responseDtoList.size());
        verify(discountRepository, times(1)).findAll();
    }

    @Test
    void testGetAllDiscounts_Empty() {
        when(discountRepository.findAll()).thenReturn(Collections.emptyList());
        List<DiscountResponseDto> responseDtoList = discountService.getAllDiscounts();
        assertEquals(0, responseDtoList.size());
        verify(discountRepository, times(1)).findAll();
    }

    private DiscountRequestDto createValidDiscountRequestDto() {
        return DiscountRequestDto.builder()
                .name("Test Discount")
                .description("Discount Description")
                .discountPercentage(new BigDecimal("0.5"))
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();
    }
}
