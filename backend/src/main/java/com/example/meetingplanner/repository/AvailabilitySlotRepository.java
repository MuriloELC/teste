package com.example.meetingplanner.repository;

import com.example.meetingplanner.model.AvailabilitySlot;
import com.example.meetingplanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    List<AvailabilitySlot> findByUsuario(User usuario);
    List<AvailabilitySlot> findByUsuarioAndDiaDaSemana(User usuario, DayOfWeek dia);
    List<AvailabilitySlot> findByUsuarioAndDataEspecifica(User usuario, LocalDate data);
}
