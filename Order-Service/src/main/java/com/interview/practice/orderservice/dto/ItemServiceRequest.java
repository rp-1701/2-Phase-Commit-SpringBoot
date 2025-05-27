package com.interview.practice.orderservice.dto;

import com.interview.practice.orderservice.model.Order;
import lombok.Data;

@Data
public class ItemServiceRequest {
    private Long orderId;
    private Long itemId;
    
    public static ItemServiceRequest fromOrder(Order order) {
        ItemServiceRequest request = new ItemServiceRequest();
        request.setOrderId(order.getId());
        request.setItemId(order.getItemId());
        return request;
    }
} 