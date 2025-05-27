package com.interview.practice.orderservice.simulation;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("simulation")
public class SimulationRunner implements CommandLineRunner {

    private final OrderSimulator orderSimulator;

    public SimulationRunner(OrderSimulator orderSimulator) {
        this.orderSimulator = orderSimulator;
    }

    @Override
    public void run(String... args) {
        orderSimulator.runSimulation();
    }
} 