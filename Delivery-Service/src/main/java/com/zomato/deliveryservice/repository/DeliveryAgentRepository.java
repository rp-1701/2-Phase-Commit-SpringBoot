package com.zomato.deliveryservice.repository;

import com.zomato.deliveryservice.model.DeliveryAgent;
import com.zomato.deliveryservice.model.AgentStatus;
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
    Optional<DeliveryAgent> findByReservedForOrderId(Long orderId);
} 