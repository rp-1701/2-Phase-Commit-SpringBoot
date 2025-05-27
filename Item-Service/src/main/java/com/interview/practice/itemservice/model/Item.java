package com.interview.practice.itemservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "items")
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;
    
    private String name;
    private Long storeId;
    private Integer quantityAvailable = 0;
    private Integer quantityReserved = 0;
    
    @Version
    private Long version = 0L;  // Initialize version to 0

    @PrePersist
    protected void onCreate() {
        if (version == null) {
            version = 0L;
        }
    }

    public boolean canReserveOne() {
        return quantityAvailable > quantityReserved;
    }

    public void reserveOne() {
        if (!canReserveOne()) {
            throw new IllegalStateException("Cannot reserve item: " + itemId);
        }
        quantityReserved++;
    }

    public void commitReservation() {
        if (quantityReserved <= 0) {
            throw new IllegalStateException("No reservation to commit for item: " + itemId);
        }
        quantityAvailable--;
        quantityReserved--;
    }

    public void releaseReservation() {
        if (quantityReserved <= 0) {
            throw new IllegalStateException("No reservation to release for item: " + itemId);
        }
        quantityReserved--;
    }
} 