package com.interview.practice.itemservice.dto;

import lombok.Data;

@Data
public class ParticipantResponse {
    private boolean ready;
    private String message;
    
    public static ParticipantResponse ready() {
        ParticipantResponse response = new ParticipantResponse();
        response.setReady(true);
        response.setMessage("Ready to commit");
        return response;
    }
    
    public static ParticipantResponse abort(String reason) {
        ParticipantResponse response = new ParticipantResponse();
        response.setReady(false);
        response.setMessage(reason);
        return response;
    }
} 