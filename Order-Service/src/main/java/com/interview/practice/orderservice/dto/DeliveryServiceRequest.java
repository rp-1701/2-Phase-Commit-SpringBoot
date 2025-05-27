package com.interview.practice.orderservice.dto;

import com.interview.practice.orderservice.model.Order;
import lombok.Data;

@Data
public class DeliveryServiceRequest {
    private Long orderId;
    private String deliveryLocation;
    
    public static DeliveryServiceRequest fromOrder(Order order) {
        DeliveryServiceRequest request = new DeliveryServiceRequest();
        request.setOrderId(order.getId());
        request.setDeliveryLocation(order.getDeliveryLocation());
        return request;
    }
} 