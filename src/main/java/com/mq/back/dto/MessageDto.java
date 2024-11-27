package com.mq.back.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageDto(UUID partnerId, String content, LocalDateTime receptionTime) {
}