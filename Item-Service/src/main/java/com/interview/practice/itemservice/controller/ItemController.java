package com.interview.practice.itemservice.controller;

import com.interview.practice.itemservice.dto.ItemServiceRequest;
import com.interview.practice.itemservice.dto.ParticipantServiceResponse;
import com.interview.practice.itemservice.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/prepare")
    public ResponseEntity<ParticipantServiceResponse> prepare(@RequestBody ItemServiceRequest request) {
        boolean prepared = itemService.prepare(request.getOrderId(), request.getItemId());
        
        if (prepared) {
            return ResponseEntity.ok(ParticipantServiceResponse.ready());
        } else {
            return ResponseEntity.ok(
                ParticipantServiceResponse.abort("Item not available or cannot be reserved")
            );
        }
    }

    @PostMapping("/commit")
    public ResponseEntity<Void> commit(@RequestBody ItemServiceRequest request) {
        itemService.commit(request.getOrderId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rollback")
    public ResponseEntity<Void> rollback(@RequestBody ItemServiceRequest request) {
        itemService.rollback(request.getOrderId());
        return ResponseEntity.ok().build();
    }
}