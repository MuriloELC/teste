package com.example.meetingplanner.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class MeetingSuggestionRequest {
    @NotEmpty
    private Set<Long> participantesIds;
    @NotNull
    private OffsetDateTime inicioJanela;
    @NotNull
    private OffsetDateTime fimJanela;
    @Min(15)
    private int duracaoMinutos;
}
