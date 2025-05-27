package com.interview.practice.orderservice.model;

public enum OrderStatus {
    INITIATED,      // Order just created
    PREPARING,      // During 2PC prepare phase
    COMMITTED,      // Successfully completed
    ABORTED         // Failed or rolled back
} 