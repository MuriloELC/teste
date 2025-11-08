package com.example.meetingplanner.model;

import com.example.meetingplanner.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role papel;

    @OneToMany(mappedBy = "responsavel")
    @Builder.Default
    private Set<Task> tarefasResponsavel = new HashSet<>();

    @OneToMany(mappedBy = "criador")
    @Builder.Default
    private Set<Task> tarefasCriadas = new HashSet<>();

    @OneToMany(mappedBy = "organizador")
    @Builder.Default
    private Set<Meeting> reunioesOrganizadas = new HashSet<>();

    @ManyToMany(mappedBy = "participantes")
    @Builder.Default
    private Set<Meeting> reunioesParticipante = new HashSet<>();

    public ZoneId getZoneId() {
        return timezone != null ? ZoneId.of(timezone) : ZoneId.systemDefault();
    }
}
