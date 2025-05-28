package com.interview.practice.orderservice.service;

import com.interview.practice.orderservice.dto.OrderRequest;
import com.interview.practice.orderservice.dto.OrderResponse;
import com.interview.practice.orderservice.dto.DeliveryServiceRequest;
import com.interview.practice.orderservice.dto.ItemServiceRequest;
import com.interview.practice.orderservice.dto.ParticipantServiceResponse;
import com.interview.practice.orderservice.model.Order;
import com.interview.practice.orderservice.model.OrderStatus;
import com.interview.practice.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    
    private static final String ITEM_SERVICE_URL = "http://localhost:8081";
    private static final String DELIVERY_SERVICE_URL = "http://localhost:8082";

    public OrderResponse createOrder(OrderRequest request) {
        try {
            log.info("Creating order for customer {} for item {} to be delivered at {}", 
                    request.getCustomerId(), request.getItemId(), request.getDeliveryLocation());
            
            // Create initial order
            Order order = new Order();
            order.setCustomerId(request.getCustomerId());
            order.setItemId(request.getItemId());
            order.setDeliveryLocation(request.getDeliveryLocation());
            order.setStatus(OrderStatus.INITIATED);
            order = orderRepository.save(order);

            // Start 2PC
            boolean success = executeTwoPhaseCommit(order);
            
            if (!success) {
                order.setStatus(OrderStatus.ABORTED);
                orderRepository.save(order);
                log.info("Order {} aborted due to resource unavailability", order.getId());
                return OrderResponse.failure();
            } else {
                order.setStatus(OrderStatus.COMMITTED);
                orderRepository.save(order);
                log.info("Order {} successfully committed", order.getId());
                return OrderResponse.success();
            }
        } catch (Exception e) {
            log.error("Error processing order: {}", e.getMessage());
            return OrderResponse.failure();
        }
    }

    private boolean executeTwoPhaseCommit(Order order) {
        order.setStatus(OrderStatus.PREPARING);
        orderRepository.save(order);
        log.info("Starting 2PC for order {}", order.getId());

        try {
            // First, try to reserve the item
            ItemServiceRequest itemRequest = ItemServiceRequest.fromOrder(order);
            ParticipantServiceResponse itemResponse = restTemplate.postForObject(
                ITEM_SERVICE_URL + "/api/items/prepare",
                itemRequest,
                ParticipantServiceResponse.class
            );

            if (itemResponse == null || !itemResponse.isReady()) {
                log.info("Item service rejected order {}. Response: {}", 
                    order.getId(), itemResponse != null ? itemResponse.getMessage() : "null");
                return false;
            }
            log.info("Item service prepared for order {}", order.getId());

            // Then, try to reserve a delivery agent
            DeliveryServiceRequest deliveryRequest = DeliveryServiceRequest.fromOrder(order);
            ParticipantServiceResponse deliveryResponse = restTemplate.postForObject(
                DELIVERY_SERVICE_URL + "/api/delivery/prepare",
                deliveryRequest,
                ParticipantServiceResponse.class
            );

            if (deliveryResponse == null || !deliveryResponse.isReady()) {
                log.info("Delivery service rejected order {}. Response: {}", 
                    order.getId(), deliveryResponse != null ? deliveryResponse.getMessage() : "null");
                rollback(order);
                return false;
            }
            log.info("Delivery service prepared for order {}", order.getId());

            // Phase 2: Commit
            return commit(order);
        } catch (Exception e) {
            log.error("Error in 2PC for order {}: {}", order.getId(), e.getMessage());
            rollback(order);
            return false;
        }
    }

    private boolean commit(Order order) {
        try {
            log.info("Starting commit phase for order {}", order.getId());
            
            ItemServiceRequest itemRequest = ItemServiceRequest.fromOrder(order);
            restTemplate.postForObject(
                ITEM_SERVICE_URL + "/api/items/commit",
                itemRequest,
                Void.class
            );
            log.info("Item service committed for order {}", order.getId());

            DeliveryServiceRequest deliveryRequest = DeliveryServiceRequest.fromOrder(order);
            restTemplate.postForObject(
                DELIVERY_SERVICE_URL + "/api/delivery/commit",
                deliveryRequest,
                Void.class
            );
            log.info("Delivery service committed for order {}", order.getId());

            return true;
        } catch (Exception e) {
            log.error("Error in commit phase for order {}: {}", order.getId(), e.getMessage());
            rollback(order);
            return false;
        }
    }

    private void rollback(Order order) {
        try {
            log.info("Starting rollback for order {}", order.getId());
            
            ItemServiceRequest itemRequest = ItemServiceRequest.fromOrder(order);
            restTemplate.postForObject(
                ITEM_SERVICE_URL + "/api/items/rollback",
                itemRequest,
                Void.class
            );
            log.info("Item service rolled back for order {}", order.getId());

            DeliveryServiceRequest deliveryRequest = DeliveryServiceRequest.fromOrder(order);
            restTemplate.postForObject(
                DELIVERY_SERVICE_URL + "/api/delivery/rollback",
                deliveryRequest,
                Void.class
            );
            log.info("Delivery service rolled back for order {}", order.getId());
        } catch (Exception e) {
            log.error("Error in rollback for order {}: {}", order.getId(), e.getMessage());
        }
    }
} 