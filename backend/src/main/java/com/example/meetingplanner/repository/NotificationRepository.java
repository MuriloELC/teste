package com.example.meetingplanner.repository;

import com.example.meetingplanner.model.Notification;
import com.example.meetingplanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUsuarioOrderByDataHoraCriacaoDesc(User usuario);
    long countByUsuarioAndLidaFalse(User usuario);
}
