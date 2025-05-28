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
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Long storeId;
    
    @Column(nullable = false)
    private Integer quantityAvailable = 0;
    
    @Column(nullable = false)
    private Integer quantityReserved = 0;
    
    @Version
    @Column(nullable = false)
    private Long version = 0L; // optimistic locking

    public boolean canReserveOne() {
        return quantityAvailable > quantityReserved && quantityAvailable > 0;
    }

    public void reserveOne() {
        if (!canReserveOne()) {
            throw new IllegalStateException("Cannot reserve item: " + itemId + ". Available: " + quantityAvailable + ", Reserved: " + quantityReserved);
        }
        quantityReserved++;
    }

    public void commitReservation() {
        if (quantityReserved <= 0) {
            throw new IllegalStateException("No reservation to commit for item: " + itemId);
        }
        if (quantityAvailable <= 0) {
            throw new IllegalStateException("No items available to commit for item: " + itemId);
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