package com.mq.back.dto;

import com.mq.back.entities.Direction;
import com.mq.back.entities.ProcessedFlowType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public record PartnerDto(@NotBlank @Size(max = 255) String alias,
                         @NotBlank @Size(max = 50) String type,
                         @NotNull Direction direction,
                         @Size(max = 65535) String application,
                         ProcessedFlowType processedFlowType,
                         @NotBlank @Size(max = 65535) String description,
                         @PastOrPresent LocalDateTime createdAt) {

}
