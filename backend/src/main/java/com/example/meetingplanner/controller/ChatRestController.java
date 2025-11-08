package com.example.meetingplanner.controller;

import com.example.meetingplanner.dto.ChatMessageRequest;
import com.example.meetingplanner.dto.ChatMessageResponse;
import com.example.meetingplanner.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private final ChatService chatService;

    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatMessageResponse> sendMessage(@RequestBody @Valid ChatMessageRequest request) {
        return ResponseEntity.ok(chatService.enviarMensagem(request));
    }

    @GetMapping
    public ResponseEntity<List<ChatMessageResponse>> list(@RequestParam String canal) {
        return ResponseEntity.ok(chatService.listarMensagens(canal));
    }
}
