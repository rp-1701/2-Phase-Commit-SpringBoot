package com.zomato.itemservice.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private Long orderId;
    private Long itemId;
} 