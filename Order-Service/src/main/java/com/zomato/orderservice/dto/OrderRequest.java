package com.zomato.orderservice.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private Long customerId;
    private Long itemId;
} 