package com.interview.practice.itemservice.dto;

import lombok.Data;

@Data
public class ParticipantServiceResponse {
    private boolean ready;
    private String message;

    public static ParticipantServiceResponse ready() {
        ParticipantServiceResponse response = new ParticipantServiceResponse();
        response.setReady(true);
        response.setMessage("Item is available and reserved");
        return response;
    }
    
    public static ParticipantServiceResponse abort(String reason) {
        ParticipantServiceResponse response = new ParticipantServiceResponse();
        response.setReady(false);
        response.setMessage(reason);
        return response;
    }
} 