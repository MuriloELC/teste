package com.example.meetingplanner.service;

import com.example.meetingplanner.dto.MeetingRequest;
import com.example.meetingplanner.dto.MeetingResponse;
import com.example.meetingplanner.model.Meeting;
import com.example.meetingplanner.model.User;
import com.example.meetingplanner.model.enums.MeetingStatus;
import com.example.meetingplanner.model.enums.NotificationType;
import com.example.meetingplanner.repository.MeetingRepository;
import com.example.meetingplanner.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public MeetingService(MeetingRepository meetingRepository,
                          UserRepository userRepository,
                          NotificationService notificationService) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public MeetingResponse create(MeetingRequest request) {
        User organizador = userRepository.findById(request.getOrganizadorId())
                .orElseThrow(() -> new IllegalArgumentException("Organizador não encontrado"));
        Set<User> participantes = carregarParticipantes(request.getParticipantesIds());
        Meeting meeting = Meeting.builder()
                .titulo(request.getTitulo())
                .descricao(request.getDescricao())
                .organizador(organizador)
                .participantes(participantes)
                .dataHoraInicio(request.getDataHoraInicio())
                .dataHoraFim(request.getDataHoraFim())
                .status(request.getStatus() != null ? request.getStatus() : MeetingStatus.AGENDADA)
                .linkVideoconferencia(request.getLinkVideoconferencia())
                .localizacao(request.getLocalizacao())
                .build();
        Meeting saved = meetingRepository.save(meeting);
        participantes.forEach(p -> notificationService.notificar(p, NotificationType.NOVA_REUNIAO,
                "Você foi convidado para a reunião: " + saved.getTitulo()));
        return toResponse(saved);
    }

    public List<MeetingResponse> listForUser(User usuario) {
        return meetingRepository.findAllByUser(usuario).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public MeetingResponse update(Long id, MeetingRequest request) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reunião não encontrada"));
        meeting.setTitulo(request.getTitulo());
        meeting.setDescricao(request.getDescricao());
        meeting.setDataHoraInicio(request.getDataHoraInicio());
        meeting.setDataHoraFim(request.getDataHoraFim());
        if (request.getStatus() != null) {
            meeting.setStatus(request.getStatus());
        }
        meeting.setLinkVideoconferencia(request.getLinkVideoconferencia());
        meeting.setLocalizacao(request.getLocalizacao());
        if (request.getParticipantesIds() != null) {
            meeting.setParticipantes(carregarParticipantes(request.getParticipantesIds()));
        }
        Meeting saved = meetingRepository.save(meeting);
        saved.getParticipantes().forEach(p -> notificationService.notificar(p, NotificationType.REUNIAO_ATUALIZADA,
                "Reunião atualizada: " + saved.getTitulo()));
        return toResponse(saved);
    }

    public void cancel(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reunião não encontrada"));
        meeting.setStatus(MeetingStatus.CANCELADA);
        meetingRepository.save(meeting);
        meeting.getParticipantes().forEach(p -> notificationService.notificar(p, NotificationType.REUNIAO_ATUALIZADA,
                "Reunião cancelada: " + meeting.getTitulo()));
    }

    private Set<User> carregarParticipantes(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        return ids.stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Participante não encontrado")))
                .collect(Collectors.toSet());
    }

    public MeetingResponse toResponse(Meeting meeting) {
        return MeetingResponse.builder()
                .id(meeting.getId())
                .titulo(meeting.getTitulo())
                .descricao(meeting.getDescricao())
                .dataHoraInicio(meeting.getDataHoraInicio())
                .dataHoraFim(meeting.getDataHoraFim())
                .status(meeting.getStatus())
                .linkVideoconferencia(meeting.getLinkVideoconferencia())
                .localizacao(meeting.getLocalizacao())
                .organizadorId(meeting.getOrganizador() != null ? meeting.getOrganizador().getId() : null)
                .organizadorNome(meeting.getOrganizador() != null ? meeting.getOrganizador().getNome() : null)
                .participantes(meeting.getParticipantes().stream()
                        .map(user -> MeetingResponse.ParticipantSummary.builder()
                                .id(user.getId())
                                .nome(user.getNome())
                                .email(user.getEmail())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }
}
