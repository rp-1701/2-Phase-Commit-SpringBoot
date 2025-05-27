package com.interview.practice.deliveryservice.service;

import com.interview.practice.deliveryservice.model.DeliveryAgent;
import com.interview.practice.deliveryservice.repository.DeliveryAgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryAgentRepository agentRepository;

    @Transactional
    public DeliveryAgent prepare(Long orderId, String deliveryLocation) {
        try {
            // Find an available agent in the delivery location
            DeliveryAgent agent = agentRepository.findAvailableAgentInLocation(deliveryLocation)
                .orElse(null);

            if (agent != null && agent.isAvailable()) {
                agent.reserve(orderId);
                return agentRepository.save(agent);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public void commit(Long orderId) {
        DeliveryAgent agent = agentRepository.findByReservedForOrderId(orderId)
            .orElseThrow(() -> new IllegalStateException("No agent reserved for order: " + orderId));

        agent.commit();
        agentRepository.save(agent);
    }

    @Transactional
    public void rollback(Long orderId) {
        agentRepository.findByReservedForOrderId(orderId)
            .ifPresent(agent -> {
                agent.release();
                agentRepository.save(agent);
            });
    }
} 