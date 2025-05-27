package com.interview.practice.deliveryservice.dto;

import lombok.Data;

@Data
public class DeliveryRequest {
    private Long orderId;
    private String deliveryLocation;
} 