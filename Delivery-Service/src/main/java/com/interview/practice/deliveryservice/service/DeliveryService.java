package com.interview.practice.deliveryservice.service;

import com.interview.practice.deliveryservice.model.DeliveryAgent;
import com.interview.practice.deliveryservice.repository.DeliveryAgentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryAgentRepository agentRepository;

    @Transactional
    public DeliveryAgent prepare(Long orderId, String deliveryLocation) {
        try {
            log.info("Starting to prepare delivery agent for order {} in location {}", orderId, deliveryLocation);
            // Find an available agent in the delivery location
            DeliveryAgent agent = agentRepository.findFirstAvailableAgentInLocation(deliveryLocation)
                .orElse(null);

            if (agent == null) {
                log.info("No agent found for order {} in location {}", orderId, deliveryLocation);
                return null;
            }

            if (!agent.isAvailable()) {
                log.info("Agent {} found but not available for order {}. Status: {}", 
                        agent.getAgentId(), orderId, agent.getStatus());
                return null;
            }

            log.info("Found available agent {} with status {} for order {}", 
                     agent.getAgentId(), agent.getStatus(), orderId);
            agent.reserve(orderId);
            agent = agentRepository.save(agent);
            log.info("Successfully reserved agent {} (status: {}) for order {}", 
                     agent.getAgentId(), agent.getStatus(), orderId);
            return agent;
        } catch (Exception e) {
            log.error("Error preparing delivery agent for order {}: {}", orderId, e.getMessage(), e);
            return null;
        }
    }

    @Transactional
    public void commit(Long orderId) {
        try {
            log.info("Starting to commit delivery agent for order {}", orderId);
            DeliveryAgent agent = agentRepository.findByReservedForOrderId(orderId)
                .orElseThrow(() -> new IllegalStateException("No agent reserved for order: " + orderId));

            log.info("Found agent {} for commit of order {}. Current status: {}", 
                    agent.getAgentId(), orderId, agent.getStatus());
            agent.commit();
            agent = agentRepository.save(agent);
            log.info("Successfully committed delivery agent {} (new status: {}) for order {}", 
                    agent.getAgentId(), agent.getStatus(), orderId);
        } catch (Exception e) {
            log.error("Error committing delivery agent for order {}: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void rollback(Long orderId) {
        try {
            log.info("Starting rollback for order {}", orderId);
            agentRepository.findByReservedForOrderId(orderId)
                .ifPresent(agent -> {
                    log.info("Found agent {} to rollback for order {}. Current status: {}", 
                            agent.getAgentId(), orderId, agent.getStatus());
                    agent.release();
                    agent = agentRepository.save(agent);
                    log.info("Successfully rolled back delivery agent {} (new status: {}) for order {}", 
                            agent.getAgentId(), agent.getStatus(), orderId);
                });
        } catch (Exception e) {
            log.error("Error rolling back delivery agent for order {}: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }
} 