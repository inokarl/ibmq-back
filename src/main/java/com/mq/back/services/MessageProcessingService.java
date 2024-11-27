package com.mq.back.services;

import com.ibm.jakarta.jms.JMSMessage;
import com.mq.back.dto.MessageDto;
import com.mq.back.entities.Message;
import com.mq.back.entities.Partner;
import com.mq.back.repositories.MessageRepository;
import com.mq.back.repositories.PartnerRepository;
import jakarta.jms.JMSException;
import jakarta.jms.MessageFormatException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MessageProcessingService {

    public static final String QUEUE_NAME = "MAIN_QUEUE";

    private final JmsTemplate jmsTemplate;
    private final MessageRepository messageRepository;
    private final PartnerRepository partnerRepository;

    @Autowired
    public MessageProcessingService(JmsTemplate jmsTemplate,
                                    MessageRepository messageRepository,
                                    PartnerRepository partnerRepository) {
        this.jmsTemplate = jmsTemplate;
        this.messageRepository = messageRepository;
        this.partnerRepository = partnerRepository;
    }

    public UUID receiveAndSaveMessage() {
        MessageDto messageDto = receiveMessage();
        Message message = saveMessage(messageDto);
        return message.getId();
    }

    private MessageDto receiveMessage() {
        try {
            JMSMessage message = (JMSMessage) jmsTemplate.receive(QUEUE_NAME);
            if (message == null) {
                throw new IllegalStateException("No message available in the queue.");
            }
            UUID partnerId = extractPartnerIdFromMessage(message);
            String content = message.getBody(String.class);
            LocalDateTime receptionTime = LocalDateTime.now();
            return new MessageDto(partnerId, content, receptionTime);
        }  catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    private UUID extractPartnerIdFromMessage(JMSMessage message) {
        try {
            String partnerIdentifier = message.getStringProperty("partner_id");
            return UUID.fromString(partnerIdentifier);
        } catch (Exception e) {
            throw new RuntimeException("Could not extract partner ID from message");
        }
    }

    @Transactional
    private Message saveMessage(MessageDto messageDto) {
        Partner partner = findPartner(messageDto.partnerId());
        Message message = new Message();
        message.setPartner(partner);
        message.setContent(messageDto.content());
        message.setReceivedAt(messageDto.receptionTime());
        return messageRepository.save(message);
    }

    @Transactional
    private Partner findPartner(UUID partnerId) {
        return partnerRepository
                .findById(partnerId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find partner with ID: " + partnerId.toString()));
    }


/*
    @Transactional
    public void retrieveAndSaveMessage(String queueName) {
        try {
            // Retrieve message from the queue
            JMSMessage jmsMessage = jmsTemplate.receive(queueName);
            if (jmsMessage == null) {
                throw new IllegalStateException("No message available in the queue.");
            }

            // Extract content from the JMS message
            String content = jmsMessage.getBody(String.class);
            String partnerAlias = jmsMessage.getStringProperty("partnerAlias"); // Example of a custom JMS property

            // Find the partner by alias (you can adapt this logic based on your requirements)
            Partner partner = partnerRepository.findByAlias(partnerAlias)
                    .orElseThrow(() -> new IllegalArgumentException("Partner not found for alias: " + partnerAlias));

            // Map to the Message entity
            Message message = new Message();
            message.setPartner(partner);
            message.setContent(content);
            message.setReceivedAt(LocalDateTime.now());

            // Save the message to the database
            messageRepository.save(message);

            System.out.println("Message saved successfully.");

        } catch (JMSException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to process message from queue.", e);
        }
    }
    */

}
