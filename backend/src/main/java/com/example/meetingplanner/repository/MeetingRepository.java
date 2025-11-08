package com.example.meetingplanner.repository;

import com.example.meetingplanner.model.Meeting;
import com.example.meetingplanner.model.User;
import com.example.meetingplanner.model.enums.MeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query("select distinct m from Meeting m left join m.participantes p where p = :user or m.organizador = :user")
    List<Meeting> findAllByUser(@Param("user") User user);

    List<Meeting> findByOrganizadorAndStatus(User organizador, MeetingStatus status);

    List<Meeting> findByDataHoraInicioBetween(OffsetDateTime inicio, OffsetDateTime fim);
}
