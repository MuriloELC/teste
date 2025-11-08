package com.example.meetingplanner.controller;

import com.example.meetingplanner.dto.MeetingRequest;
import com.example.meetingplanner.dto.MeetingResponse;
import com.example.meetingplanner.dto.MeetingSuggestionRequest;
import com.example.meetingplanner.dto.MeetingSuggestionResponse;
import com.example.meetingplanner.model.User;
import com.example.meetingplanner.repository.UserRepository;
import com.example.meetingplanner.service.MeetingService;
import com.example.meetingplanner.service.SchedulingAssistantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingService meetingService;
    private final SchedulingAssistantService schedulingAssistantService;
    private final UserRepository userRepository;

    public MeetingController(MeetingService meetingService,
                             SchedulingAssistantService schedulingAssistantService,
                             UserRepository userRepository) {
        this.meetingService = meetingService;
        this.schedulingAssistantService = schedulingAssistantService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<MeetingResponse> create(@RequestBody @Valid MeetingRequest request) {
        return ResponseEntity.ok(meetingService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<MeetingResponse>> list(@AuthenticationPrincipal UserDetails principal) {
        User usuario = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        return ResponseEntity.ok(meetingService.listForUser(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeetingResponse> update(@PathVariable Long id,
                                                  @RequestBody @Valid MeetingRequest request) {
        return ResponseEntity.ok(meetingService.update(id, request));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        meetingService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/suggestions")
    public ResponseEntity<List<MeetingSuggestionResponse>> suggestions(@RequestBody @Valid MeetingSuggestionRequest request) {
        return ResponseEntity.ok(schedulingAssistantService.sugerirHorarios(request));
    }
}
