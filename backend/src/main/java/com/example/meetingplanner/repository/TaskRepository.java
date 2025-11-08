package com.example.meetingplanner.repository;

import com.example.meetingplanner.model.Task;
import com.example.meetingplanner.model.User;
import com.example.meetingplanner.model.enums.TaskPriority;
import com.example.meetingplanner.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    long countByResponsavelAndStatus(User responsavel, TaskStatus status);
    long countByResponsavelAndPrioridade(User responsavel, TaskPriority prioridade);
}
