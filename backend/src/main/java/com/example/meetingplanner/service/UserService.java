package com.example.meetingplanner.service;

import com.example.meetingplanner.dto.UserRegistrationRequest;
import com.example.meetingplanner.dto.UserResponse;
import com.example.meetingplanner.model.User;
import com.example.meetingplanner.model.enums.Role;
import com.example.meetingplanner.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        User user = User.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .timezone(request.getTimezone())
                .papel(Role.USER)
                .build();
        return userRepository.save(user);
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nome(user.getNome())
                .email(user.getEmail())
                .timezone(user.getTimezone())
                .papel(user.getPapel().name())
                .build();
    }
}
