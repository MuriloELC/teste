package com.example.meetingplanner.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ChatMessageResponse {
    private Long id;
    private Long remetenteId;
    private String remetenteNome;
    private Long meetingId;
    private String canal;
    private String conteudo;
    private OffsetDateTime dataHoraEnvio;
}
