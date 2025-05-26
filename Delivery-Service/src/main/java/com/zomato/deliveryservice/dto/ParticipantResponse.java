package com.zomato.deliveryservice.dto;

import lombok.Data;

@Data
public class ParticipantResponse {
    private boolean ready;
    private String message;
    private Long assignedAgentId;  // Additional field specific to delivery service

    public static ParticipantResponse ready(Long agentId) {
        ParticipantResponse response = new ParticipantResponse();
        response.setReady(true);
        response.setMessage("Delivery agent is ready");
        response.setAssignedAgentId(agentId);
        return response;
    }

    public static ParticipantResponse abort(String reason) {
        ParticipantResponse response = new ParticipantResponse();
        response.setReady(false);
        response.setMessage(reason);
        response.setAssignedAgentId(null);
        return response;
    }
} 