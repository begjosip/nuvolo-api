package com.nuvolo.nuvoloapi.redis.data;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShoppingSession {

    private Long userId;

    private BigDecimal total;

    private LocalDateTime createdAt;

    private List<CartItem> cartItems;

}
