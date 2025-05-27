package com.interview.practice.orderservice.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private Long customerId;
    private Long itemId;
    private String deliveryLocation;
} 