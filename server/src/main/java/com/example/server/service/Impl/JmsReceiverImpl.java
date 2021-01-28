package com.example.server.service.Impl;

import com.example.common.dto.MessageDTO;
import com.example.common.utils.ResponseCode;
import com.example.common.utils.ResponseMessage;
import com.example.server.service.JmsReceiver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Service
@AllArgsConstructor
public class JmsReceiverImpl implements JmsReceiver {

    private final OperationServiceImpl operationsService;
    private final KafkaTemplate<String, ResponseMessage> kafkaTemplate;
    private final KafkaConsumer<String, MessageDTO> kafkaConsumer;

    public void receiveAndSend(String requestQueue, String responseQueue) {
        log.debug("message received from queue: {}", requestQueue);
        String key = "";
        TopicPartition tp = new TopicPartition(requestQueue, 0);
        ConsumerRecords<String, MessageDTO> records;
        ResponseCode responseCode = ResponseCode.ERROR;
        MessageDTO messageDTO;
        kafkaConsumer.assign(Collections.singletonList(tp));
        kafkaConsumer.seekToBeginning(Collections.singletonList(tp));
        while(true) {
            records = kafkaConsumer.poll(Duration.ofMillis(1000));
            if (records.count() != 0) {
                for (ConsumerRecord<String, MessageDTO> record : records) {
                    log.debug("received message {}", record);
                    key = record.key();
                    messageDTO = record.value();
                    switch (messageDTO.getOperations()) {
                        case "ADD_AMOUNT":
                            responseCode = operationsService.addAmount(messageDTO);
                            break;
                        case "WITHDRAW":
                            responseCode = operationsService.withdraw(messageDTO);
                            break;
                        case "TRANSFER":
                            responseCode = operationsService.transfer(messageDTO);
                            break;
                        case "CREATE_ACCOUNT":
                            responseCode = operationsService.createAccount(messageDTO);
                            break;
                        case "CLOSE_ACCOUNT":
                            responseCode = operationsService.closeAccount(messageDTO);
                            break;
                        default:
                            System.exit(0);
                    }
                    createMessage(responseCode, responseQueue, key);

                }
            }
        }
    }

    private void createMessage(ResponseCode code, String responseQueue, String key) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setCode(code);
        ListenableFuture<SendResult<String, ResponseMessage>> responseToSentRequest = kafkaTemplate.send(responseQueue, key, responseMessage);
        kafkaTemplate.flush();
        System.out.println("отправлено в очкередь: " + responseToSentRequest);
        log.info("sent request: {}", responseToSentRequest);
        log.debug("sent response message, to queue: {} {}", responseMessage, responseQueue);
    }
}
