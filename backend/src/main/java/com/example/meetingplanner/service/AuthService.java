package com.example.meetingplanner.service;

import com.example.meetingplanner.dto.AuthResponse;
import com.example.meetingplanner.dto.LoginRequest;
import com.example.meetingplanner.dto.UserRegistrationRequest;
import com.example.meetingplanner.model.User;
import com.example.meetingplanner.repository.UserRepository;
import com.example.meetingplanner.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       UserService userService,
                       UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public User register(UserRegistrationRequest request) {
        return userService.registerUser(request);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        return new AuthResponse(token,
                new AuthResponse.UserSummary(user.getId(), user.getNome(), user.getEmail(), user.getPapel().name()));
    }
}
