package com.example.meetingplanner.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationRequest {

    @NotBlank
    private String nome;

    @Email
    private String email;

    @Size(min = 6)
    private String senha;

    private String timezone;
}
