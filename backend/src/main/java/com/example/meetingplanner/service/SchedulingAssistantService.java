package com.example.meetingplanner.service;

import com.example.meetingplanner.dto.MeetingSuggestionRequest;
import com.example.meetingplanner.dto.MeetingSuggestionResponse;
import com.example.meetingplanner.model.AvailabilitySlot;
import com.example.meetingplanner.model.Meeting;
import com.example.meetingplanner.model.Task;
import com.example.meetingplanner.model.User;
import com.example.meetingplanner.repository.AvailabilitySlotRepository;
import com.example.meetingplanner.repository.MeetingRepository;
import com.example.meetingplanner.repository.TaskRepository;
import com.example.meetingplanner.repository.UserRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SchedulingAssistantService {

    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final MeetingRepository meetingRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public SchedulingAssistantService(AvailabilitySlotRepository availabilitySlotRepository,
                                      MeetingRepository meetingRepository,
                                      TaskRepository taskRepository,
                                      UserRepository userRepository) {
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.meetingRepository = meetingRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<MeetingSuggestionResponse> sugerirHorarios(MeetingSuggestionRequest request) {
        List<User> participantes = request.getParticipantesIds().stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id)))
                .collect(Collectors.toList());

        Map<Long, List<Meeting>> meetingsPorUsuario = participantes.stream()
                .collect(Collectors.toMap(User::getId, meetingRepository::findAllByUser));
        Map<Long, List<Task>> tasksPorUsuario = participantes.stream()
                .collect(Collectors.toMap(User::getId,
                        user -> taskRepository.findAll((Specification<Task>) (root, query, cb) -> cb.equal(root.get("responsavel"), user))));

        OffsetDateTime inicioJanela = request.getInicioJanela();
        OffsetDateTime fimJanela = request.getFimJanela();
        long durationMinutes = request.getDuracaoMinutos();

        List<OffsetDateTime> candidatos = gerarCandidatos(inicioJanela, fimJanela, durationMinutes);
        List<MeetingSuggestionResponse> respostas = new ArrayList<>();

        for (OffsetDateTime inicio : candidatos) {
            OffsetDateTime fim = inicio.plusMinutes(durationMinutes);
            boolean disponivel = true;
            double score = 0;
            for (User participante : participantes) {
                if (!estaDentroDaDisponibilidade(participante, inicio, fim)) {
                    disponivel = false;
                    break;
                }
                double conflito = possuiConflitos(participante, inicio, fim,
                        meetingsPorUsuario.getOrDefault(participante.getId(), List.of()),
                        tasksPorUsuario.getOrDefault(participante.getId(), List.of()));
                if (conflito > 0) {
                    disponivel = false;
                    break;
                }
                score += calcularPontuacao(participante, inicio);
            }
            if (disponivel) {
                respostas.add(MeetingSuggestionResponse.builder()
                        .dataHoraInicio(inicio)
                        .dataHoraFim(fim)
                        .pontuacao(score / participantes.size())
                        .build());
            }
        }

        return respostas.stream()
                .sorted(Comparator.comparing(MeetingSuggestionResponse::getPontuacao).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    private List<OffsetDateTime> gerarCandidatos(OffsetDateTime inicio, OffsetDateTime fim, long durationMinutes) {
        List<OffsetDateTime> candidatos = new ArrayList<>();
        OffsetDateTime cursor = inicio.truncatedTo(ChronoUnit.MINUTES);
        while (!cursor.plusMinutes(durationMinutes).isAfter(fim)) {
            candidatos.add(cursor);
            cursor = cursor.plusMinutes(30);
        }
        return candidatos;
    }

    private boolean estaDentroDaDisponibilidade(User user, OffsetDateTime inicio, OffsetDateTime fim) {
        ZoneId zone = user.getZoneId();
        OffsetDateTime inicioZona = inicio.atZoneSameInstant(zone).toOffsetDateTime();
        OffsetDateTime fimZona = fim.atZoneSameInstant(zone).toOffsetDateTime();
        List<AvailabilitySlot> slots = availabilitySlotRepository.findByUsuario(user);
        return slots.stream().anyMatch(slot -> {
            if (slot.getHoraInicio() == null || slot.getHoraFim() == null) {
                return false;
            }
            if (slot.getDataEspecifica() != null) {
                if (!slot.getDataEspecifica().equals(inicioZona.toLocalDate())) {
                    return false;
                }
                return !inicioZona.toLocalTime().isBefore(slot.getHoraInicio())
                        && !fimZona.toLocalTime().isAfter(slot.getHoraFim());
            }
            if (slot.getDiaDaSemana() != null) {
                if (!slot.getDiaDaSemana().equals(inicioZona.getDayOfWeek())) {
                    return false;
                }
                return !inicioZona.toLocalTime().isBefore(slot.getHoraInicio())
                        && !fimZona.toLocalTime().isAfter(slot.getHoraFim());
            }
            return false;
        });
    }

    private double possuiConflitos(User user, OffsetDateTime inicio, OffsetDateTime fim,
                                   List<Meeting> meetings, List<Task> tasks) {
        for (Meeting meeting : meetings) {
            if (meeting.getDataHoraInicio() == null || meeting.getDataHoraFim() == null) {
                continue;
            }
            if (overlaps(inicio, fim, meeting.getDataHoraInicio(), meeting.getDataHoraFim())) {
                return 1;
            }
        }
        for (Task task : tasks) {
            if (task.getDataHoraInicioPlanejada() != null && task.getDataHoraFimPlanejada() != null
                    && overlaps(inicio, fim, task.getDataHoraInicioPlanejada(), task.getDataHoraFimPlanejada())) {
                return 1;
            }
        }
        return 0;
    }

    private boolean overlaps(OffsetDateTime start1, OffsetDateTime end1, OffsetDateTime start2, OffsetDateTime end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    private double calcularPontuacao(User user, OffsetDateTime inicio) {
        // Estratégia simples: reuniões em horário comercial recebem maior pontuação
        int hour = inicio.atZoneSameInstant(user.getZoneId()).getHour();
        if (hour >= 9 && hour <= 18) {
            return 1.0;
        }
        return 0.5;
    }
}
