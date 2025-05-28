package com.interview.practice.deliveryservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "delivery_agents")
@Data
public class DeliveryAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agentId;
    
    private String name;

    private String currentLocation;
    
    @Enumerated(EnumType.STRING)
    private AgentStatus status = AgentStatus.AVAILABLE;
    
    @Version
    private Long version = 0L;
    
    private Long reservedForOrderId;

    public boolean isAvailable() {
        return status == AgentStatus.AVAILABLE && reservedForOrderId == null;
    }

    public void reserve(Long orderId) {
        if (!isAvailable()) {
            throw new IllegalStateException("Agent not available: " + agentId);
        }
        status = AgentStatus.RESERVED;
        reservedForOrderId = orderId;
    }

    public void commit() {
        if (status != AgentStatus.RESERVED) {
            throw new IllegalStateException("Agent not in RESERVED state: " + agentId);
        }
        status = AgentStatus.ASSIGNED;
    }

    public void release() {
        if (status != AgentStatus.RESERVED) {
            throw new IllegalStateException("Agent not in RESERVED state: " + agentId);
        }
        status = AgentStatus.AVAILABLE;
        reservedForOrderId = null;
    }
} 