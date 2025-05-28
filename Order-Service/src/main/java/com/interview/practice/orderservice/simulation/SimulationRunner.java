package com.interview.practice.orderservice.simulation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("test") // Only run this in the test profile
public class SimulationRunner implements CommandLineRunner {

    private final OrderSimulator orderSimulator;

    public SimulationRunner(OrderSimulator orderSimulator) {
        this.orderSimulator = orderSimulator;
    }

    @Override
    public void run(String... args) {
        try {
            log.info("Starting order simulation...");
            orderSimulator.runSimulation();
            log.info("Order simulation completed successfully");
        } catch (Exception e) {
            log.error("Error during order simulation: {}", e.getMessage(), e);
            // Don't rethrow the exception as we want the application to continue running
        }
    }
} 