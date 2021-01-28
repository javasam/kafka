package com.example.client.service;

import com.example.client.service.impl.JmsSenderImpl;
import com.example.common.dto.MessageDTO;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.Message;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class JmsSenderServiceTest extends TestCase {

    @InjectMocks
    JmsSenderImpl jmsSender;

    @Mock
    MessageConverter messageConverter;

    @Mock
    JmsTemplate jmsTemplate;

    @Mock
    Message message;

    @Captor
    ArgumentCaptor<String> uuidCaptor;

    @Captor
    ArgumentCaptor<String> queueCaptor;
    @Test
    public void sendAndReceiveMessageTest() {
        String requestQueue = "123";
        String responseQueue = "124";
        MessageDTO messageDTO = MessageDTO
                .builder()
                .bankClientName("Ivan")
                .bankAccountClientId("123456")
                .amount("2000")
                .build();
        doNothing().when(jmsTemplate).convertAndSend(requestQueue, messageDTO, message -> message);
        when(jmsTemplate.receiveSelected(anyString(), anyString())).thenCallRealMethod();
        jmsSender.sendAndReceiveMessage(requestQueue, responseQueue, messageDTO);
        verify(jmsTemplate).receiveSelected(queueCaptor.capture(), uuidCaptor.capture());
        assertNotNull(uuidCaptor.getValue());
        assertEquals(responseQueue, queueCaptor.getValue());
    }
}
