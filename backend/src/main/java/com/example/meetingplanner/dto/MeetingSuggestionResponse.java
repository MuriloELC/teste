package com.example.meetingplanner.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class MeetingSuggestionResponse {
    private OffsetDateTime dataHoraInicio;
    private OffsetDateTime dataHoraFim;
    private double pontuacao;
}
