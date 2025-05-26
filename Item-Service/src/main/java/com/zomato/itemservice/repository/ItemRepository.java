package com.zomato.itemservice.repository;

import com.zomato.itemservice.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Item> findByItemId(Long itemId);
} 