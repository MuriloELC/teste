package com.example.meetingplanner.model;

import com.example.meetingplanner.model.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User usuario;

    @Enumerated(EnumType.STRING)
    private NotificationType tipo;

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    private boolean lida;

    private OffsetDateTime dataHoraCriacao;
}
