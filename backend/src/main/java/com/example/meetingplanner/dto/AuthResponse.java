package com.example.meetingplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserSummary user;

    @Data
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String nome;
        private String email;
        private String papel;
    }
}
