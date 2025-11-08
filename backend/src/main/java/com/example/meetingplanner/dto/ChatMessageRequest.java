package com.example.meetingplanner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatMessageRequest {
    @NotNull
    private Long remetenteId;
    private Long meetingId;
    @NotBlank
    private String canal;
    @NotBlank
    private String conteudo;
}
