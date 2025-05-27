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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    
    private static final String ITEM_SERVICE_URL = "http://localhost:8081";
    private static final String DELIVERY_SERVICE_URL = "http://localhost:8082";

    public OrderResponse createOrder(OrderRequest request) {
        try {
            // Create initial order
            Order order = new Order();
            order.setCustomerId(request.getCustomerId());
            order.setItemId(request.getItemId());
            order.setDeliveryLocation(request.getDeliveryLocation());
            order = orderRepository.save(order);

            // Start 2PC
            boolean success = executeTwoPhaseCommit(order);
            
            if (!success) {
                order.setStatus(OrderStatus.ABORTED);
                orderRepository.save(order);
                return OrderResponse.failure("Required resources are not available at the moment.");
            } else {
                order.setStatus(OrderStatus.COMMITTED);
                orderRepository.save(order);
                return OrderResponse.success();
            }
        } catch (Exception e) {
            return OrderResponse.failure("An unexpected error occurred.");
        }
    }

    private boolean executeTwoPhaseCommit(Order order) {
        // Phase 1: Prepare
        order.setStatus(OrderStatus.PREPARING);
        orderRepository.save(order);

        try {
            // Ask participants to prepare
            ItemServiceRequest itemRequest = ItemServiceRequest.fromOrder(order);
            ParticipantServiceResponse itemResponse = restTemplate.postForObject(
                ITEM_SERVICE_URL + "/api/items/prepare",
                itemRequest,
                ParticipantServiceResponse.class
            );

            DeliveryServiceRequest deliveryRequest = DeliveryServiceRequest.fromOrder(order);
            ParticipantServiceResponse deliveryResponse = restTemplate.postForObject(
                DELIVERY_SERVICE_URL + "/api/delivery/prepare",
                deliveryRequest,
                ParticipantServiceResponse.class
            );

            // If any participant is not ready, abort
            if (itemResponse == null || !itemResponse.isReady()) {
                rollback(order);
                return false;
            }
            
            if (deliveryResponse == null || !deliveryResponse.isReady()) {
                rollback(order);
                return false;
            }

            // Phase 2: Commit
            return commit(order);
        } catch (Exception e) {
            rollback(order);
            return false;
        }
    }

    private boolean commit(Order order) {
        try {
            ItemServiceRequest itemRequest = ItemServiceRequest.fromOrder(order);
            restTemplate.postForObject(
                ITEM_SERVICE_URL + "/api/items/commit",
                itemRequest,
                Void.class
            );

            DeliveryServiceRequest deliveryRequest = DeliveryServiceRequest.fromOrder(order);
            restTemplate.postForObject(
                DELIVERY_SERVICE_URL + "/api/delivery/commit",
                deliveryRequest,
                Void.class
            );

            return true;
        } catch (Exception e) {
            rollback(order);
            return false;
        }
    }

    private void rollback(Order order) {
        try {
            ItemServiceRequest itemRequest = ItemServiceRequest.fromOrder(order);
            restTemplate.postForObject(
                ITEM_SERVICE_URL + "/api/items/rollback",
                itemRequest,
                Void.class
            );

            DeliveryServiceRequest deliveryRequest = DeliveryServiceRequest.fromOrder(order);
            restTemplate.postForObject(
                DELIVERY_SERVICE_URL + "/api/delivery/rollback",
                deliveryRequest,
                Void.class
            );
        } catch (Exception e) {
            // Log rollback failure
        }
    }
} 