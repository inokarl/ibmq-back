package com.mq.back.services;

import com.mq.back.dto.MessageDto;
import com.mq.back.entities.Message;
import com.mq.back.repositories.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    void receiveAndSaveMessage_ShouldReturnMessageId() {
        // Arrange
        String messageContent = "Test Message";
        MessageDto messageDto = new MessageDto(messageContent, LocalDateTime.now());
        Message savedMessage = new Message();
        savedMessage.setId(UUID.randomUUID());
        savedMessage.setContent(messageContent);
        savedMessage.setReceivedAt(messageDto.receptionTime());

        Mockito.when(jmsTemplate.receiveAndConvert(MessageService.QUEUE_NAME)).thenReturn(messageContent);
        Mockito.when(messageRepository.save(Mockito.any(Message.class))).thenReturn(savedMessage);
        // Act
        UUID result = messageService.receiveAndSaveMessage();
        // Assert
        assertNotNull(result);
        Mockito.verify(jmsTemplate, Mockito.times(1)).receiveAndConvert(MessageService.QUEUE_NAME);
        Mockito.verify(messageRepository, Mockito.times(1)).save(Mockito.any(Message.class));
    }

    @Test
    void getMessages_ShouldReturnMessagesPageSortedByOldest() {
        // Arrange
        Pageable pageable = PageRequest.of(0, MessageService.PAGE_SIZE, Sort.by("receivedAt").ascending());
        List<Message> messages = List.of(new Message(), new Message());
        Page<Message> messagePage = new PageImpl<>(messages);
        Mockito.when(messageRepository.findAll(pageable)).thenReturn(messagePage);
        // Act
        Page<Message> result = messageService.getMessages(0, true);
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        Mockito.verify(messageRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    void getMessages_ShouldReturnMessagesPageSortedByNewest() {
        // Arrange
        Pageable pageable = PageRequest.of(0, MessageService.PAGE_SIZE, Sort.by("receivedAt").descending());
        List<Message> messages = List.of(new Message(), new Message());
        Page<Message> messagePage = new PageImpl<>(messages);
        Mockito.when(messageRepository.findAll(pageable)).thenReturn(messagePage);
        // Act
        Page<Message> result = messageService.getMessages(0, false);
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        Mockito.verify(messageRepository, Mockito.times(1)).findAll(pageable);
    }
}