package com.interview.practice.itemservice.controller;

import com.interview.practice.itemservice.dto.OrderRequest;
import com.interview.practice.itemservice.dto.ParticipantResponse;
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
    public ResponseEntity<ParticipantResponse> prepare(@RequestBody OrderRequest request) {
        boolean prepared = itemService.prepare(request.getOrderId(), request.getItemId());
        
        if (prepared) {
            return ResponseEntity.ok(ParticipantResponse.ready());
        } else {
            return ResponseEntity.ok(
                ParticipantResponse.abort("Item not available or cannot be reserved")
            );
        }
    }

    @PostMapping("/commit")
    public ResponseEntity<Void> commit(@RequestBody OrderRequest request) {
        itemService.commit(request.getOrderId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rollback")
    public ResponseEntity<Void> rollback(@RequestBody OrderRequest request) {
        itemService.rollback(request.getOrderId());
        return ResponseEntity.ok().build();
    }
}