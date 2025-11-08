package com.example.meetingplanner.dto;

import com.example.meetingplanner.model.enums.MeetingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
@Builder
public class MeetingResponse {
    private Long id;
    private String titulo;
    private String descricao;
    private OffsetDateTime dataHoraInicio;
    private OffsetDateTime dataHoraFim;
    private MeetingStatus status;
    private String linkVideoconferencia;
    private String localizacao;
    private Long organizadorId;
    private String organizadorNome;
    private Set<ParticipantSummary> participantes;

    @Data
    @Builder
    public static class ParticipantSummary {
        private Long id;
        private String nome;
        private String email;
    }
}
