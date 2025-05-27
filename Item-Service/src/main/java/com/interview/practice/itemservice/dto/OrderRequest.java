package com.interview.practice.itemservice.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private Long orderId;
    private Long itemId;
} 