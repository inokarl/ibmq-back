package com.mq.back.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageDto(String content, LocalDateTime receptionTime) {
}