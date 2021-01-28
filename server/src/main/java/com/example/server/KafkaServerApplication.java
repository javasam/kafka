package com.example.server;

import com.example.server.service.Impl.JmsReceiverImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class KafkaServerApplication implements ApplicationRunner {

    @Autowired
    private JmsReceiverImpl jmsReceiver;


    @Value("${kafka.request.queue}")
    private String request_queue;

    @Value("${kafka.response.queue}")
    private String response_queue;

    public static void main(String[] args) {
        SpringApplication.run(KafkaServerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        jmsReceiver.receiveAndSend(request_queue, response_queue);
    }
}
