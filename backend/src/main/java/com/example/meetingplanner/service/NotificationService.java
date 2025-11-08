package com.example.meetingplanner.service;

import com.example.meetingplanner.dto.NotificationResponse;
import com.example.meetingplanner.model.Notification;
import com.example.meetingplanner.model.User;
import com.example.meetingplanner.model.enums.NotificationType;
import com.example.meetingplanner.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void notificar(User usuario, NotificationType tipo, String mensagem) {
        Notification notification = Notification.builder()
                .usuario(usuario)
                .tipo(tipo)
                .mensagem(mensagem)
                .lida(false)
                .dataHoraCriacao(OffsetDateTime.now())
                .build();
        Notification saved = notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + usuario.getId(), toResponse(saved));
    }

    public List<NotificationResponse> listar(User usuario) {
        return notificationRepository.findByUsuarioOrderByDataHoraCriacaoDesc(usuario).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void marcarComoLida(Long notificationId, User usuario) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada"));
        if (!notification.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Sem permissão para atualizar a notificação");
        }
        notification.setLida(true);
        notificationRepository.save(notification);
    }

    public NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .tipo(notification.getTipo())
                .mensagem(notification.getMensagem())
                .lida(notification.isLida())
                .dataHoraCriacao(notification.getDataHoraCriacao())
                .build();
    }
}
