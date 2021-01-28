package com.example.client.service;

import com.example.common.dto.MessageDTO;

public interface KafkaSender {
    String sendAndReceiveMessage(String requestQueue, String responseQueue, MessageDTO messageDTO);
}
