package com.interview.practice.orderservice.simulation;

import com.interview.practice.orderservice.dto.OrderRequest;
import com.interview.practice.orderservice.dto.OrderResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class OrderSimulator {

    private static final String ORDER_SERVICE_URL = "http://localhost:8080/api/orders";
    private static final int CONCURRENT_ORDERS = 1;
    private final RestTemplate restTemplate;

    public OrderSimulator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void runSimulation() {
        System.out.println("Starting simulation with " + CONCURRENT_ORDERS + " concurrent orders...");

        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_ORDERS);
        List<CompletableFuture<OrderResponse>> futures = new ArrayList<>();

        for (int i = 0; i < CONCURRENT_ORDERS; i++) {
            final int orderId = i + 1;
            CompletableFuture<OrderResponse> future = CompletableFuture.supplyAsync(() -> {
                OrderRequest request = new OrderRequest();
                request.setCustomerId(1L + orderId);
                request.setItemId(1L);
                request.setDeliveryLocation("Koramangala");

                System.out.println("Placing order " + orderId);
                return restTemplate.postForObject(ORDER_SERVICE_URL, request, OrderResponse.class);
            }, executorService);

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenAccept(v -> {
                    System.out.println("\nAll orders completed. Results:");
                    for (int i = 0; i < futures.size(); i++) {
                        try {
                            OrderResponse response = futures.get(i).get();
                            System.out.println("Order " + i + ": " + response.getMessage());
                        } catch (Exception e) {
                            System.out.println("Order " + i + " failed: " + e.getMessage());
                        }
                    }
                })
                .join();

        executorService.shutdown();
    }
} 