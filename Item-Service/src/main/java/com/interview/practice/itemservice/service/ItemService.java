package com.interview.practice.itemservice.service;

import com.interview.practice.itemservice.model.Item;
import com.interview.practice.itemservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    
    // Track prepared transactions: orderId -> itemId
    private final Map<Long, Long> preparedOrders = new ConcurrentHashMap<>();

    @Transactional
    public boolean prepare(Long orderId, Long itemId) {
        try {
            log.debug("Preparing item {} for order {}", itemId, orderId);
            Item item = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));
            
            log.debug("Found item with version: {}", item.getVersion());

            if (item.canReserveOne()) {
                item.reserveOne();
                log.debug("Before save - Item version: {}", item.getVersion());
                item = itemRepository.save(item);
                log.debug("After save - Item version: {}", item.getVersion());
                preparedOrders.put(orderId, itemId);
                log.debug("Successfully prepared item {} for order {}", itemId, orderId);
                return true;
            }
            log.debug("Cannot reserve item {} for order {}", itemId, orderId);
            return false;
        } catch (Exception e) {
            log.error("Error preparing item {} for order {}: {}", itemId, orderId, e.getMessage());
            return false;
        }
    }

    @Transactional
    public void commit(Long orderId) {
        Long itemId = preparedOrders.remove(orderId);
        if (itemId == null) {
            log.error("No prepared transaction found for order: {}", orderId);
            throw new IllegalStateException("No prepared transaction found for order: " + orderId);
        }

        try {
            log.debug("Committing item {} for order {}", itemId, orderId);
            Item item = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

            item.commitReservation();
            itemRepository.save(item);
            log.debug("Successfully committed item {} for order {}", itemId, orderId);
        } catch (Exception e) {
            log.error("Error committing item {} for order {}: {}", itemId, orderId, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void rollback(Long orderId) {
        Long itemId = preparedOrders.remove(orderId);
        if (itemId == null) {
            log.debug("Nothing to rollback for order: {}", orderId);
            return; // Nothing to rollback
        }

        try {
            log.debug("Rolling back item {} for order {}", itemId, orderId);
            Item item = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

            item.releaseReservation();
            itemRepository.save(item);
            log.debug("Successfully rolled back item {} for order {}", itemId, orderId);
        } catch (Exception e) {
            log.error("Error rolling back item {} for order {}: {}", itemId, orderId, e.getMessage());
            throw e;
        }
    }
} 