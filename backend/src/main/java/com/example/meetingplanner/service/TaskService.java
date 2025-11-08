package com.example.meetingplanner.service;

import com.example.meetingplanner.dto.TaskRequest;
import com.example.meetingplanner.dto.TaskResponse;
import com.example.meetingplanner.model.Meeting;
import com.example.meetingplanner.model.Task;
import com.example.meetingplanner.model.User;
import com.example.meetingplanner.model.enums.NotificationType;
import com.example.meetingplanner.model.enums.TaskPriority;
import com.example.meetingplanner.model.enums.TaskStatus;
import com.example.meetingplanner.repository.MeetingRepository;
import com.example.meetingplanner.repository.TaskRepository;
import com.example.meetingplanner.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final NotificationService notificationService;

    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       MeetingRepository meetingRepository,
                       NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.meetingRepository = meetingRepository;
        this.notificationService = notificationService;
    }

    public TaskResponse createTask(TaskRequest request, User criador) {
        User responsavel = Optional.ofNullable(request.getResponsavelId())
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Responsável não encontrado")))
                .orElse(criador);
        Meeting meeting = Optional.ofNullable(request.getMeetingId())
                .flatMap(meetingRepository::findById)
                .orElse(null);
        Task task = Task.builder()
                .titulo(request.getTitulo())
                .descricao(request.getDescricao())
                .status(Optional.ofNullable(request.getStatus()).orElse(TaskStatus.PENDENTE))
                .dataHoraInicioPlanejada(request.getDataHoraInicioPlanejada())
                .dataHoraFimPlanejada(request.getDataHoraFimPlanejada())
                .dataHoraConclusao(request.getDataHoraConclusao())
                .prioridade(Optional.ofNullable(request.getPrioridade()).orElse(TaskPriority.MEDIA))
                .responsavel(responsavel)
                .criador(criador)
                .meeting(meeting)
                .build();
        Task saved = taskRepository.save(task);
        if (!responsavel.equals(criador)) {
            notificationService.notificar(responsavel, NotificationType.TAREFA_CRIADA,
                    "Nova tarefa atribuída: " + saved.getTitulo());
        }
        return toResponse(saved);
    }

    public List<TaskResponse> listTasks(User usuario, TaskStatus status, TaskPriority prioridade, OffsetDateTime ate) {
        Specification<Task> spec = Specification.where((root, query, cb) -> cb.equal(root.get("responsavel"), usuario));
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (prioridade != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("prioridade"), prioridade));
        }
        if (ate != null) {
            spec = spec.and((root, query, cb) -> {
                Predicate start = cb.lessThanOrEqualTo(root.get("dataHoraInicioPlanejada"), ate);
                Predicate end = cb.lessThanOrEqualTo(root.get("dataHoraFimPlanejada"), ate);
                return cb.or(start, end);
            });
        }
        return taskRepository.findAll(spec).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse updateTask(Long id, TaskRequest request, User usuario) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada"));
        if (!task.getResponsavel().getId().equals(usuario.getId())
                && !task.getCriador().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Sem permissão para editar a tarefa");
        }
        if (request.getTitulo() != null) {
            task.setTitulo(request.getTitulo());
        }
        if (request.getDescricao() != null) {
            task.setDescricao(request.getDescricao());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        task.setDataHoraInicioPlanejada(request.getDataHoraInicioPlanejada());
        task.setDataHoraFimPlanejada(request.getDataHoraFimPlanejada());
        task.setDataHoraConclusao(request.getDataHoraConclusao());
        if (request.getPrioridade() != null) {
            task.setPrioridade(request.getPrioridade());
        }
        if (request.getResponsavelId() != null) {
            User responsavel = userRepository.findById(request.getResponsavelId())
                    .orElseThrow(() -> new IllegalArgumentException("Responsável não encontrado"));
            task.setResponsavel(responsavel);
        }
        if (request.getMeetingId() != null) {
            Meeting meeting = meetingRepository.findById(request.getMeetingId())
                    .orElseThrow(() -> new IllegalArgumentException("Reunião não encontrada"));
            task.setMeeting(meeting);
        }
        Task saved = taskRepository.save(task);
        notificationService.notificar(task.getResponsavel(), NotificationType.TAREFA_ATUALIZADA,
                "Tarefa atualizada: " + task.getTitulo());
        return toResponse(saved);
    }

    public void deleteTask(Long id, User usuario) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada"));
        if (!task.getCriador().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Sem permissão para excluir a tarefa");
        }
        taskRepository.delete(task);
    }

    public TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .titulo(task.getTitulo())
                .descricao(task.getDescricao())
                .status(task.getStatus())
                .dataHoraInicioPlanejada(task.getDataHoraInicioPlanejada())
                .dataHoraFimPlanejada(task.getDataHoraFimPlanejada())
                .dataHoraConclusao(task.getDataHoraConclusao())
                .prioridade(task.getPrioridade())
                .responsavelId(task.getResponsavel() != null ? task.getResponsavel().getId() : null)
                .responsavelNome(task.getResponsavel() != null ? task.getResponsavel().getNome() : null)
                .meetingId(task.getMeeting() != null ? task.getMeeting().getId() : null)
                .build();
    }
}
