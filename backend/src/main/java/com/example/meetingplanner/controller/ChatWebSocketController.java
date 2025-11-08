package com.example.meetingplanner.controller;

import com.example.meetingplanner.dto.ChatMessageRequest;
import com.example.meetingplanner.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    private final ChatService chatService;

    public ChatWebSocketController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat.send")
    public void handleChat(@Valid ChatMessageRequest request) {
        chatService.enviarMensagem(request);
    }
}
