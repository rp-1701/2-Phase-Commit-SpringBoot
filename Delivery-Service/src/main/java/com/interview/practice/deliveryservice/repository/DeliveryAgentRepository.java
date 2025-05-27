package com.interview.practice.deliveryservice.repository;

import com.interview.practice.deliveryservice.model.DeliveryAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT da FROM DeliveryAgent da WHERE da.status = 'AVAILABLE' AND da.currentLocation = :location ORDER BY da.agentId ASC LIMIT 1")
    Optional<DeliveryAgent> findAvailableAgentInLocation(String location);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT da FROM DeliveryAgent da WHERE da.reservedForOrderId = :orderId")
    Optional<DeliveryAgent> findByReservedForOrderId(Long orderId);
} 