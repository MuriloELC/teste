package com.example.meetingplanner.dto;

import com.example.meetingplanner.model.enums.MeetingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class MeetingRequest {
    @NotBlank
    private String titulo;
    private String descricao;
    @NotNull
    private OffsetDateTime dataHoraInicio;
    @NotNull
    private OffsetDateTime dataHoraFim;
    private MeetingStatus status;
    private String linkVideoconferencia;
    private String localizacao;
    @NotNull
    private Long organizadorId;
    private Set<Long> participantesIds;
}
