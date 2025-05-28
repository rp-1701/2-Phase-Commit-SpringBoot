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
            log.info("Preparing delivery agent for order {} in location {}", orderId, deliveryLocation);
            DeliveryAgent agent = agentRepository.findFirstAvailableAgentInLocation(deliveryLocation)
                .orElse(null);

            if (agent == null) {
                log.info("No available agent found for order {} in location {}", orderId, deliveryLocation);
                return null;
            }

            agent.reserve(orderId);
            agent = agentRepository.save(agent);
            log.info("Successfully reserved agent {} for order {}", agent.getAgentId(), orderId);
            return agent;
        } catch (Exception e) {
            log.error("Error preparing delivery agent for order {}: {}", orderId, e.getMessage());
            return null;
        }
    }

    @Transactional
    public void commit(Long orderId) {
        try {
            log.info("Committing delivery agent for order {}", orderId);
            DeliveryAgent agent = agentRepository.findByReservedForOrderId(orderId)
                .orElseThrow(() -> new IllegalStateException("No agent reserved for order: " + orderId));

            agent.commit();
            agent = agentRepository.save(agent);
            log.info("Successfully committed agent {} for order {}", agent.getAgentId(), orderId);
        } catch (Exception e) {
            log.error("Error committing delivery agent for order {}: {}", orderId, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void rollback(Long orderId) {
        try {
            log.info("Rolling back delivery agent for order {}", orderId);
            agentRepository.findByReservedForOrderId(orderId)
                .ifPresent(agent -> {
                    agent.release();
                    agent = agentRepository.save(agent);
                    log.info("Successfully rolled back agent {} for order {}", agent.getAgentId(), orderId);
                });
        } catch (Exception e) {
            log.error("Error rolling back delivery agent for order {}: {}", orderId, e.getMessage());
            throw e;
        }
    }
} 