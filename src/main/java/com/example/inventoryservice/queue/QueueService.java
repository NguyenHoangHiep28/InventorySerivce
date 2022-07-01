package com.example.inventoryservice.queue;

import com.example.inventoryservice.entity.enums.InventoryStatus;
import com.example.inventoryservice.event.OrderEvent;
import com.example.inventoryservice.exception.RequestNotValidException;
import com.example.inventoryservice.service.ProductIEService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class QueueService {
    private final ProductIEService productIEService;

    public QueueService(ProductIEService productIEService) {
        this.productIEService = productIEService;
    }

    public OrderEvent handlePendingOrder(OrderEvent orderEvent) {
        log.info(orderEvent.toString());
//        if (!orderEvent.validationInventory()) {
//            throw new RequestNotValidException("Order is not valid!" + orderEvent.toString());
//        }

        boolean productsExportSuccess = productIEService.exportProductsToOrder(orderEvent.getOrderId(), orderEvent.getOrderDetailDTOSet());
        if (productsExportSuccess) {
            orderEvent.setInventoryStatus(InventoryStatus.PROCESSED);
            log.info("Export products successfully!");
        } else {
            orderEvent.setInventoryStatus(InventoryStatus.FAILED);
            orderEvent.setMessage("Lack of products.");
            log.info("Export products failed!");
        }
        return orderEvent;
    }

    public OrderEvent handleReturnProducts(OrderEvent orderEvent) {
//        log.info(orderEvent.toString());
//        if (!orderEvent.validationInventory()) {
//            throw new RequestNotValidException("Order is not valid!" + orderEvent.toString());
//        }
        boolean productsReturnSuccess = productIEService.returnProductsFromOrder(orderEvent.getOrderId());
        if (productsReturnSuccess) {
            log.info("Send notification that products have been returned successfully!");
        } else {
            log.info("Send notification that products have been returned failed!");
        }
        return orderEvent;
    }
}
