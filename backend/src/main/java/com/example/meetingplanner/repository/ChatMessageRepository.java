package com.example.meetingplanner.repository;

import com.example.meetingplanner.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByCanalOrderByDataHoraEnvioAsc(String canal);
    List<ChatMessage> findByMeetingIdOrderByDataHoraEnvioAsc(Long meetingId);
}
