package com.interview.practice.deliveryservice.model;

public enum AgentStatus {
    AVAILABLE,  // Agent is free to take orders
    RESERVED,   // Agent is temporarily reserved during 2PC
    ASSIGNED,   // Agent is assigned to an order
} 