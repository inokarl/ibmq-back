package com.mq.back.services;

import com.mq.back.dto.MessageDto;
import com.mq.back.entities.Message;
import com.mq.back.repositories.MessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    public static final String QUEUE_NAME = "MAIN_QUEUE";
    public static final int PAGE_SIZE = 10;

    private final JmsTemplate jmsTemplate;
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(JmsTemplate jmsTemplate,
                          MessageRepository messageRepository) {
        this.jmsTemplate = jmsTemplate;
        this.messageRepository = messageRepository;
    }

    public UUID receiveAndSaveMessage() {
        MessageDto messageDto = receiveMessage();
        Message message = saveMessage(messageDto);
        return message.getId();
    }

    private MessageDto receiveMessage() {
        String content = jmsTemplate.receiveAndConvert(QUEUE_NAME).toString();
        LocalDateTime receptionTime = LocalDateTime.now();
        return new MessageDto(content, receptionTime);
    }

    @Transactional
    private Message saveMessage(MessageDto messageDto) {
        Message message = new Message();
        message.setContent(messageDto.content());
        message.setReceivedAt(messageDto.receptionTime());
        return messageRepository.save(message);
    }

    public Page<Message> getMessages(int page, boolean orderByOldest) {
        Sort sort = orderByOldest ? Sort.by("receivedAt").ascending() : Sort.by("receivedAt").descending();
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);
        return messageRepository.findAll(pageable);
    }
}
