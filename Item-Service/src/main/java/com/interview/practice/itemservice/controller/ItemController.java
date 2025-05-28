package com.interview.practice.itemservice.controller;

import com.interview.practice.itemservice.dto.ItemServiceRequest;
import com.interview.practice.itemservice.dto.ParticipantServiceResponse;
import com.interview.practice.itemservice.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/prepare")
    public ResponseEntity<ParticipantServiceResponse> prepare(@RequestBody ItemServiceRequest request) {
        try {
            log.info("Received prepare request for orderId: {}, itemId: {}", request.getOrderId(), request.getItemId());
            
            boolean prepared = itemService.prepare(request.getOrderId(), request.getItemId());
            
            if (prepared) {
                log.info("Successfully prepared item for orderId: {}", request.getOrderId());
                return ResponseEntity.ok(ParticipantServiceResponse.ready());
            } else {
                log.warn("Could not prepare item for orderId: {}", request.getOrderId());
                return ResponseEntity.ok(
                    ParticipantServiceResponse.abort("Item not available or cannot be reserved")
                );
            }
        } catch (Exception e) {
            log.error("Error preparing item for orderId: " + request.getOrderId(), e);
            return ResponseEntity.ok(
                ParticipantServiceResponse.abort("Internal error: " + e.getMessage())
            );
        }
    }

    @PostMapping("/commit")
    public ResponseEntity<Void> commit(@RequestBody ItemServiceRequest request) {
        try {
            log.info("Received commit request for orderId: {}", request.getOrderId());
            itemService.commit(request.getOrderId(), request.getItemId());
            log.info("Successfully committed item for orderId: {}", request.getOrderId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error committing item for orderId: " + request.getOrderId(), e);
            throw e;
        }
    }

    @PostMapping("/rollback")
    public ResponseEntity<Void> rollback(@RequestBody ItemServiceRequest request) {
        try {
            log.info("Received rollback request for orderId: {}", request.getOrderId());
            itemService.rollback(request.getOrderId(), request.getItemId());
            log.info("Successfully rolled back item for orderId: {}", request.getOrderId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error rolling back item for orderId: " + request.getOrderId(), e);
            throw e;
        }
    }
}