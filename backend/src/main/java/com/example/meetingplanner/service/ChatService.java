package com.example.meetingplanner.service;

import com.example.meetingplanner.dto.ChatMessageRequest;
import com.example.meetingplanner.dto.ChatMessageResponse;
import com.example.meetingplanner.model.ChatMessage;
import com.example.meetingplanner.model.Meeting;
import com.example.meetingplanner.model.User;
import com.example.meetingplanner.repository.ChatMessageRepository;
import com.example.meetingplanner.repository.MeetingRepository;
import com.example.meetingplanner.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(ChatMessageRepository chatMessageRepository,
                       UserRepository userRepository,
                       MeetingRepository meetingRepository,
                       SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.meetingRepository = meetingRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public ChatMessageResponse enviarMensagem(ChatMessageRequest request) {
        User remetente = userRepository.findById(request.getRemetenteId())
                .orElseThrow(() -> new IllegalArgumentException("Remetente não encontrado"));
        Meeting meeting = Optional.ofNullable(request.getMeetingId())
                .flatMap(meetingRepository::findById)
                .orElse(null);
        ChatMessage message = ChatMessage.builder()
                .remetente(remetente)
                .meeting(meeting)
                .canal(request.getCanal())
                .conteudo(request.getConteudo())
                .dataHoraEnvio(OffsetDateTime.now())
                .build();
        ChatMessage saved = chatMessageRepository.save(message);
        ChatMessageResponse response = toResponse(saved);
        messagingTemplate.convertAndSend("/topic/chat/" + request.getCanal(), response);
        return response;
    }

    public List<ChatMessageResponse> listarMensagens(String canal) {
        return chatMessageRepository.findByCanalOrderByDataHoraEnvioAsc(canal).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ChatMessageResponse toResponse(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .remetenteId(chatMessage.getRemetente() != null ? chatMessage.getRemetente().getId() : null)
                .remetenteNome(chatMessage.getRemetente() != null ? chatMessage.getRemetente().getNome() : null)
                .meetingId(chatMessage.getMeeting() != null ? chatMessage.getMeeting().getId() : null)
                .canal(chatMessage.getCanal())
                .conteudo(chatMessage.getConteudo())
                .dataHoraEnvio(chatMessage.getDataHoraEnvio())
                .build();
    }
}
