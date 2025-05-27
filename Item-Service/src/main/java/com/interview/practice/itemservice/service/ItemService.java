package com.interview.practice.itemservice.service;

import com.interview.practice.itemservice.model.Item;
import com.interview.practice.itemservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    
    // Track prepared transactions
    private final Map<Long, Long> preparedOrders = new ConcurrentHashMap<>();

    @Transactional
    public boolean prepare(Long orderId, Long itemId) {
        try {
            Item item = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

            if (item.canReserveOne()) {
                item.reserveOne();
                itemRepository.save(item);
                preparedOrders.put(orderId, itemId);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public void commit(Long orderId) {
        Long itemId = preparedOrders.remove(orderId);
        if (itemId == null) {
            throw new IllegalStateException("No prepared transaction found for order: " + orderId);
        }

        Item item = itemRepository.findByItemId(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        item.commitReservation();
        itemRepository.save(item);
    }

    @Transactional
    public void rollback(Long orderId) {
        Long itemId = preparedOrders.remove(orderId);
        if (itemId == null) {
            return; // Nothing to rollback
        }

        Item item = itemRepository.findByItemId(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        item.releaseReservation();
        itemRepository.save(item);
    }
} 