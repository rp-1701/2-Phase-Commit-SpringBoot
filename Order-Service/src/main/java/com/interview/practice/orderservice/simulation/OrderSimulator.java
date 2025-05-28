package com.interview.practice.orderservice.simulation;

import com.interview.practice.orderservice.dto.OrderRequest;
import com.interview.practice.orderservice.dto.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class OrderSimulator {

    private static final String ORDER_SERVICE_URL = "http://localhost:8080/api/orders";
    private static final int CONCURRENT_ORDERS = 15;
    private final RestTemplate restTemplate;
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);

    public OrderSimulator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void runSimulation() {
        log.info("Starting simulation with {} concurrent orders...", CONCURRENT_ORDERS);
        Instant start = Instant.now();

        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_ORDERS);
        List<CompletableFuture<OrderResponse>> futures = new ArrayList<>();

        try {
            // Submit all orders
            for (int i = 1; i <= CONCURRENT_ORDERS; i++) {
                final int orderId = i;
                CompletableFuture<OrderResponse> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        OrderRequest request = createOrderRequest(orderId);
                        log.info("Placing order {}", orderId);
                        OrderResponse response = restTemplate.postForObject(ORDER_SERVICE_URL, request, OrderResponse.class);
                        
                        if (response != null) {
                            if (response.isOrderPlaced()) {
                                successCount.incrementAndGet();
                                log.info("Order {} succeeded: {}", orderId, response.getMessage());
                            } else {
                                failureCount.incrementAndGet();
                                log.warn("Order {} failed: {}", orderId, response.getMessage());
                            }
                        } else {
                            failureCount.incrementAndGet();
                            log.error("Order {} failed: No response received", orderId);
                        }
                        
                        return response;
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        log.error("Error processing order {}: {}", orderId, e.getMessage());
                        throw e;
                    }
                }, executorService);

                futures.add(future);
            }

            // Wait for all orders to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenAccept(v -> {
                        Duration duration = Duration.between(start, Instant.now());
                        printSimulationResults(duration);
                    })
                    .join();

        } finally {
            executorService.shutdown();
        }
    }

    private OrderRequest createOrderRequest(int orderId) {
        OrderRequest request = new OrderRequest();
        request.setCustomerId((long) orderId);
        request.setItemId(1L);
        request.setDeliveryLocation("Koramangala");
        return request;
    }

    private void printSimulationResults(Duration duration) {
        log.info("\n========== Simulation Results ==========");
        log.info("Total Orders: {}", CONCURRENT_ORDERS);
        log.info("Successful Orders: {}", successCount.get());
        log.info("Failed Orders: {}", failureCount.get());
        log.info("Total Duration: {} seconds", duration.toSeconds());
        log.info("=====================================\n");
    }
} 