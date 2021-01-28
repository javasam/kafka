package com.example.client.service.impl;

import com.example.client.service.KafkaSender;
import com.example.common.dto.MessageDTO;
import com.example.common.utils.ResponseCode;
import com.example.common.utils.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaSenderImpl implements KafkaSender {

    private final KafkaTemplate<String, MessageDTO> kafkaTemplate;
    private final KafkaConsumer<String, ResponseMessage> kafkaConsumer;

    @Override
    public String sendAndReceiveMessage(String requestQueue, String responseQueue, MessageDTO messageDTO) {
        log.debug("sending message, to queue: {}, {}", messageDTO, requestQueue);
        ResponseCode responseCode = ResponseCode.ERROR;
        Long positionCounter = -1L;
        UUID uuid = UUID.randomUUID();
        ConsumerRecords<String, ResponseMessage> records;
        TopicPartition tp = new TopicPartition(responseQueue, 0);
        kafkaTemplate.send(requestQueue, uuid.toString(), messageDTO);
        kafkaTemplate.flush();
        kafkaConsumer.assign(Collections.singletonList(tp));
        kafkaConsumer.seekToBeginning(Collections.singletonList(tp));
        while (kafkaConsumer.position(tp) > positionCounter) {
            positionCounter = kafkaConsumer.position(tp);
            records = kafkaConsumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, ResponseMessage> record : records) {
                if (record.key().equals(uuid.toString())) {
                    log.info("record.key() " + record.key());
                    responseCode = record.value().getCode();
                }
            }
        }
        return responseCode.getDescription();
    }
}
