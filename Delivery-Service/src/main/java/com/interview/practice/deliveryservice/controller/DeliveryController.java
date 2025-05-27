package com.interview.practice.deliveryservice.controller;

import com.interview.practice.deliveryservice.dto.DeliveryRequest;
import com.interview.practice.deliveryservice.dto.ParticipantResponse;
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
    public ResponseEntity<ParticipantResponse> prepare(@RequestBody DeliveryRequest request) {
        DeliveryAgent agent = deliveryService.prepare(request.getOrderId(), request.getDeliveryLocation());
        
        if (agent != null) {
            return ResponseEntity.ok(ParticipantResponse.ready(agent.getAgentId()));
        } else {
            return ResponseEntity.ok(
                ParticipantResponse.abort("No delivery agent available in the requested location")
            );
        }
    }

    @PostMapping("/commit")
    public ResponseEntity<Void> commit(@RequestBody DeliveryRequest request) {
        deliveryService.commit(request.getOrderId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rollback")
    public ResponseEntity<Void> rollback(@RequestBody DeliveryRequest request) {
        deliveryService.rollback(request.getOrderId());
        return ResponseEntity.ok().build();
    }
} 