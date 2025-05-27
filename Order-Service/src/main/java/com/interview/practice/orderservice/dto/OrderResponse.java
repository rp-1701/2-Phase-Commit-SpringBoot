package com.interview.practice.orderservice.dto;

import lombok.Data;

@Data
public class OrderResponse {
    private boolean orderPlaced;
    private String message;

    public static OrderResponse success() {
        OrderResponse response = new OrderResponse();
        response.setOrderPlaced(true);
        response.setMessage("Your order has been successfully placed!");
        return response;
    }

    public static OrderResponse failure(String reason) {
        OrderResponse response = new OrderResponse();
        response.setOrderPlaced(false);
        response.setMessage("Sorry, we couldn't place your order. " + reason);
        return response;
    }
} 