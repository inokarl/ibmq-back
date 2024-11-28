package com.mq.back.controllers;

import com.mq.back.entities.Message;
import com.mq.back.services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }


    /**
     * Retrieve a list of all messages.
     *
     * @param page The page number to retrieve (default is 0).
     * @param orderByOldest Boolean flag to sort messages by oldest (default is false).
     * @return A page of messages.
     */
    @Operation(summary = "Get all messages", description = "Retrieve a list of all messages.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @GetMapping(path = "/all")
    public Page<Message> getMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "false") boolean orderByOldest) {
        return messageService.getMessages(page, orderByOldest);
    }


    /**
     * Receive a message from the queue and save it to the database.
     *
     * @return The ID of the saved message.
     */
    @Operation(summary = "Receive and save message", description = "Receive a message from the queue and store it in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message received and saved successfully"),
            @ApiResponse(responseCode = "500", description = "Error receiving the message")
    })
    @PostMapping(path = "/receive")
    public ResponseEntity<UUID> receiveAndSaveMessage() {
        UUID messageId = messageService.receiveAndSaveMessage();
        return ResponseEntity.ok(messageId);
    }
}
