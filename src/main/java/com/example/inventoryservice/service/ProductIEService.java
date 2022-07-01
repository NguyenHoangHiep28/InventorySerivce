package com.example.inventoryservice.service;

import com.example.inventoryservice.event.OrderDetailDTO;
import com.example.inventoryservice.event.OrderEvent;

import java.util.Set;

public interface ProductIEService {
    boolean exportProductsToOrder(String orderId, Set<OrderDetailDTO> orderDetailDTOSet);
    boolean returnProductsFromOrder(String orderId);
}
