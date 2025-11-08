package com.example.meetingplanner.dto;

import com.example.meetingplanner.model.enums.TaskPriority;
import com.example.meetingplanner.model.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class TaskRequest {
    @NotBlank
    private String titulo;
    private String descricao;
    private TaskStatus status;
    private OffsetDateTime dataHoraInicioPlanejada;
    private OffsetDateTime dataHoraFimPlanejada;
    private OffsetDateTime dataHoraConclusao;
    private TaskPriority prioridade;
    private Long responsavelId;
    private Long meetingId;
}
