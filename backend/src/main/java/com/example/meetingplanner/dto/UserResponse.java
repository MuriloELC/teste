package com.example.meetingplanner.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String nome;
    private String email;
    private String timezone;
    private String papel;
}
