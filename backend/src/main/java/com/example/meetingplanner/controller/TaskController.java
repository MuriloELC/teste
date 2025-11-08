package com.example.meetingplanner.controller;

import com.example.meetingplanner.dto.TaskRequest;
import com.example.meetingplanner.dto.TaskResponse;
import com.example.meetingplanner.model.User;
import com.example.meetingplanner.model.enums.TaskPriority;
import com.example.meetingplanner.model.enums.TaskStatus;
import com.example.meetingplanner.repository.UserRepository;
import com.example.meetingplanner.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    public TaskController(TaskService taskService, UserRepository userRepository) {
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@AuthenticationPrincipal UserDetails principal,
                                               @RequestBody @Valid TaskRequest request) {
        User criador = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        return ResponseEntity.ok(taskService.createTask(request, criador));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> list(@AuthenticationPrincipal UserDetails principal,
                                                   @RequestParam(required = false) TaskStatus status,
                                                   @RequestParam(required = false) TaskPriority prioridade,
                                                   @RequestParam(required = false)
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime ate) {
        User usuario = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        return ResponseEntity.ok(taskService.listTasks(usuario, status, prioridade, ate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails principal,
                                               @RequestBody @Valid TaskRequest request) {
        User usuario = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        return ResponseEntity.ok(taskService.updateTask(id, request, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails principal) {
        User usuario = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        taskService.deleteTask(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
