package com.interview.practice.deliveryservice.dto;

import lombok.Data;

@Data
public class DeliveryServiceRequest {
    private Long orderId;
    private String deliveryLocation;
} 