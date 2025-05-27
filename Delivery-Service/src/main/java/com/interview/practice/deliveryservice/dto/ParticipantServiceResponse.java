package com.interview.practice.deliveryservice.dto;

import lombok.Data;

@Data
public class ParticipantServiceResponse {
    private boolean ready;
    private String message;
    private Long assignedAgentId;  // Additional field specific to delivery service
    
    public boolean isReady() {
        return ready;
    }
    
    public static ParticipantServiceResponse ready(Long agentId) {
        ParticipantServiceResponse response = new ParticipantServiceResponse();
        response.setReady(true);
        response.setMessage("Delivery agent is ready");
        response.setAssignedAgentId(agentId);
        return response;
    }
    
    public static ParticipantServiceResponse abort(String reason) {
        ParticipantServiceResponse response = new ParticipantServiceResponse();
        response.setReady(false);
        response.setMessage(reason);
        response.setAssignedAgentId(null);
        return response;
    }
} 