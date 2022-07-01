package com.example.inventoryservice.queue;

import com.example.inventoryservice.entity.enums.InventoryStatus;
import com.example.inventoryservice.event.OrderEvent;
import com.example.inventoryservice.exception.RequestNotValidException;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.example.inventoryservice.queue.QueuesConfig.*;

@Component
@Log4j2
public class ReceiveMessage {
    private final QueueService queueService;
    private final AmqpTemplate rabbitTemplate;
    public ReceiveMessage(QueueService queueService, AmqpTemplate rabbitTemplate) {
        this.queueService = queueService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = {QUEUE_INVENTORY, DIRECT_QUEUE_INVENTORY})
    public void handleOrder(OrderEvent orderEvent) {
        if (!orderEvent.validationInventory()) {
            throw new RequestNotValidException("Order is not valid!" + orderEvent.toString());
        }
        OrderEvent returnEvent = null;
        if (orderEvent.getInventoryStatus().equals(InventoryStatus.WAITING_FOR_PROCESSING)) {
            returnEvent = queueService.handlePendingOrder(orderEvent);
            log.info(returnEvent);
        } else if (orderEvent.getInventoryStatus().equals(InventoryStatus.RETURN)) {
            returnEvent = queueService.handleReturnProducts(orderEvent);
            log.info(returnEvent);
        }
        rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER, orderEvent);
    }


}
