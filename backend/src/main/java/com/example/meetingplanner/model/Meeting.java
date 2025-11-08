package com.example.meetingplanner.model;

import com.example.meetingplanner.model.enums.MeetingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "meetings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizador_id")
    private User organizador;

    @ManyToMany
    @JoinTable(name = "meeting_participants",
            joinColumns = @JoinColumn(name = "meeting_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default
    private Set<User> participantes = new HashSet<>();

    private OffsetDateTime dataHoraInicio;

    private OffsetDateTime dataHoraFim;

    @Enumerated(EnumType.STRING)
    private MeetingStatus status;

    private String linkVideoconferencia;

    private String localizacao;
}
