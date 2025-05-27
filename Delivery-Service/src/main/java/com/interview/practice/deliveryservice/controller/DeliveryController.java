package com.interview.practice.deliveryservice.controller;

import com.interview.practice.deliveryservice.dto.DeliveryServiceRequest;
import com.interview.practice.deliveryservice.dto.ParticipantServiceResponse;
import com.interview.practice.deliveryservice.model.DeliveryAgent;
import com.interview.practice.deliveryservice.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping("/prepare")
    public ResponseEntity<ParticipantServiceResponse> prepare(@RequestBody DeliveryServiceRequest request) {
        DeliveryAgent agent = deliveryService.prepare(request.getOrderId(), request.getDeliveryLocation());
        
        if (agent != null) {
            return ResponseEntity.ok(ParticipantServiceResponse.ready(agent.getAgentId()));
        } else {
            return ResponseEntity.ok(
                ParticipantServiceResponse.abort("No delivery agent available in the requested location")
            );
        }
    }

    @PostMapping("/commit")
    public ResponseEntity<Void> commit(@RequestBody DeliveryServiceRequest request) {
        deliveryService.commit(request.getOrderId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rollback")
    public ResponseEntity<Void> rollback(@RequestBody DeliveryServiceRequest request) {
        deliveryService.rollback(request.getOrderId());
        return ResponseEntity.ok().build();
    }
} 