package com.example.meetingplanner.dto;

import com.example.meetingplanner.model.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private NotificationType tipo;
    private String mensagem;
    private boolean lida;
    private OffsetDateTime dataHoraCriacao;
}
