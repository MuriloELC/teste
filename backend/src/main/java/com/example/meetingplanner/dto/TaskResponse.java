package com.example.meetingplanner.dto;

import com.example.meetingplanner.model.enums.TaskPriority;
import com.example.meetingplanner.model.enums.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class TaskResponse {
    private Long id;
    private String titulo;
    private String descricao;
    private TaskStatus status;
    private OffsetDateTime dataHoraInicioPlanejada;
    private OffsetDateTime dataHoraFimPlanejada;
    private OffsetDateTime dataHoraConclusao;
    private TaskPriority prioridade;
    private Long responsavelId;
    private String responsavelNome;
    private Long meetingId;
}
