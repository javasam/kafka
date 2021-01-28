package com.example.server.service;

public interface KafkaReceiver {
    void receiveAndSend(String requestQueue, String responseQueue);
}
