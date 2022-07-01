package com.example.inventoryservice.repository;

import com.example.inventoryservice.entity.IEHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface IEHistoryRepository extends JpaRepository<IEHistory, Integer> {
    Set<IEHistory> findAllByOrderId(String orderId);
}
