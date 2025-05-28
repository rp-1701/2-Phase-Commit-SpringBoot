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
            log.info("Preparing item {} for order {}", itemId, orderId);
            Item item = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));
            
            if (item.canReserveOne()) {
                item.reserveOne();
                item = itemRepository.save(item);
                //preparedOrders.put(orderId, itemId);
                log.info("Successfully reserved item {} for order {}. Available: {}, Reserved: {}", 
                        itemId, orderId, item.getQuantityAvailable(), item.getQuantityReserved());
                return true;
            }
            log.info("Cannot reserve item {} for order {}. Available: {}, Reserved: {}", 
                    itemId, orderId, item.getQuantityAvailable(), item.getQuantityReserved());
            return false;
        } catch (Exception e) {
            log.error("Error preparing item {} for order {}: {}", itemId, orderId, e.getMessage());
            return false;
        }
    }

    @Transactional
    public void commit(Long orderId, Long itemId) {
        //Long itemId = preparedOrders.remove(orderId);
        if (itemId == null) {
            log.error("No prepared transaction found for order: {}", orderId);
            throw new IllegalStateException("No prepared transaction found for order: " + orderId);
        }

        try {
            log.info("Committing item {} for order {}", itemId, orderId);
            Item item = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

            item.commitReservation();
            itemRepository.save(item);
            log.info("Successfully committed item {} for order {}", itemId, orderId);
        } catch (Exception e) {
            log.error("Error committing item {} for order {}: {}", itemId, orderId, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void rollback(Long orderId, Long itemId) {
        //Long itemId = preparedOrders.remove(orderId);
        if (itemId == null) {
            log.info("Nothing to rollback for order: {}", orderId);
            return;
        }

        try {
            log.info("Rolling back item {} for order {}", itemId, orderId);
            Item item = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

            item.releaseReservation();
            itemRepository.save(item);
            log.info("Successfully rolled back item {} for order {}", itemId, orderId);
        } catch (Exception e) {
            log.error("Error rolling back item {} for order {}: {}", itemId, orderId, e.getMessage());
            throw e;
        }
    }
} 