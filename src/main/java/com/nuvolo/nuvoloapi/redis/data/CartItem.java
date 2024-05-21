package com.nuvolo.nuvoloapi.redis.data;

import lombok.Data;

@Data
public class CartItem {

    private Long productId;

    private Integer quantity;

}
