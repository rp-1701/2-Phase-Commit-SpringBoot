package com.interview.practice.itemservice.repository;

import com.interview.practice.itemservice.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.itemId = :itemId")
    Optional<Item> findByItemId(Long itemId);
} 