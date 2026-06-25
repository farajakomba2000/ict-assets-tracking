package com.example.ictassetstracking.controller;

import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ictassetstracking.config.JwtTokenProvider;
import com.example.ictassetstracking.dto.JwtResponse;
import com.example.ictassetstracking.dto.LoginRequest;
import com.example.ictassetstracking.dto.SignupRequest;
import com.example.ictassetstracking.dto.UserCreateRequest;
import com.example.ictassetstracking.repository.EmployeeRepository;
import com.example.ictassetstracking.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final EmployeeRepository employeeRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider,
                          UserService userService,
                          EmployeeRepository employeeRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.employeeRepository = employeeRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getCheckNumber(), loginRequest.getPassword())
        );
        String token = tokenProvider.generateToken(authentication);

        Long checkNumber = Long.valueOf(loginRequest.getCheckNumber());
        return ResponseEntity.ok(new JwtResponse(token, checkNumber));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            if (signupRequest.getCheckNumber() == null || employeeRepository.findByCheckNumber(signupRequest.getCheckNumber()).isEmpty()) {
                return ResponseEntity.badRequest().body("Employee not found for check number: " + signupRequest.getCheckNumber());
            }
            com.example.ictassetstracking.entity.Employee employee = employeeRepository.findByCheckNumber(signupRequest.getCheckNumber()).get();
            UserCreateRequest request = new UserCreateRequest();
            request.setUsername(null);
            request.setPassword(signupRequest.getPassword());
            request.setCheckNumber(signupRequest.getCheckNumber());
            request.setRoles(Collections.singleton(com.example.ictassetstracking.entity.Role.ROLE_USER));
            userService.createUser(request);
            return ResponseEntity.ok("User created successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
