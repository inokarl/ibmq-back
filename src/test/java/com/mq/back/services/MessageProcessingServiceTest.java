package com.mq.back.services;

import com.ibm.jakarta.jms.JMSMessage;
import com.mq.back.entities.Message;
import com.mq.back.entities.Partner;
import com.mq.back.repositories.MessageRepository;
import com.mq.back.repositories.PartnerRepository;
import jakarta.jms.JMSException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class MessageProcessingServiceTest {

    @InjectMocks
    private MessageProcessingService messageProcessingService;

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private Partner partnerMock;

    @Mock
    private JMSMessage jmsMessage; // Mock de JMSMessage

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);  // Manually initialize mocks if not using JUnit 5 annotations
    }

    @Test
    void testReceiveAndSaveMessage_withNullPartnerId() {
        // Cas où le partenaire est null (absence de propriété 'partner_id')
        try {
            String content = "Test message content";
            System.out.println(jmsMessage.getBody(String.class));
            when(jmsTemplate.receive("MAIN_QUEUE")).thenReturn(jmsMessage);
            when(jmsMessage.getBody(String.class)).thenReturn(content);
            when(jmsMessage.getStringProperty("partner_id")).thenReturn(null);  // Pas de partner_id
            // Exécution du service
            messageProcessingService.receiveAndSaveMessage();
        } catch (Exception e) {
            // Vérifier si l'exception est bien levée
            assert e instanceof RuntimeException;
            assert e.getMessage().equals("Could not extract partner ID from message");
        }
    }

    @Test
    void testReceiveAndSaveMessage() throws JMSException {
        // Prepare test data
        UUID partnerId = UUID.randomUUID();  // Random partner ID
        String messageContent = "Test message content";
        LocalDateTime receptionTime = LocalDateTime.now();

        // Create a mock JMSMessage to return from the jmsTemplate
        JMSMessage mockMessage = mock(JMSMessage.class);
        when(mockMessage.getBody(String.class)).thenReturn(messageContent);
        when(mockMessage.getStringProperty("partner_id")).thenReturn(partnerId.toString());

        // Mock the JmsTemplate to return our mock message
        when(jmsTemplate.receive(MessageProcessingService.QUEUE_NAME)).thenReturn(mockMessage);

        // Mock the partnerRepository to return a mock partner entity
        when(partnerRepository.findById(partnerId)).thenReturn(java.util.Optional.of(partnerMock));

        // Mock the messageRepository to return a saved message (we don't actually need to persist it in this test)
        Message savedMessage = new Message();
        savedMessage.setId(UUID.randomUUID());  // Generated ID for the saved message
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        // Call the method under test
        UUID savedMessageId = messageProcessingService.receiveAndSaveMessage();

        // Assertions
        assertNotNull(savedMessageId);  // Ensure that the message has been saved with an ID
        verify(messageRepository, times(1)).save(any(Message.class));  // Verify that the save method was called exactly once
        verify(jmsTemplate, times(1)).receive(MessageProcessingService.QUEUE_NAME);  // Verify that the receive method was called once
    }

}