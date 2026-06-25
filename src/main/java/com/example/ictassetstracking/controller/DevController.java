package com.example.ictassetstracking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ictassetstracking.entity.UserAccount;
import com.example.ictassetstracking.repository.UserRepository;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/dev")
public class DevController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DevController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
        String checkNumberStr = body.get("check_number");
        String password = body.get("password");
        if (checkNumberStr == null || password == null) {
            return ResponseEntity.badRequest().body("check_number and password required");
        }
        Long checkNumber;
        try {
            checkNumber = Long.valueOf(checkNumberStr);
        } catch (NumberFormatException ex) {
            return ResponseEntity.badRequest().body("invalid check_number");
        }
        Optional<UserAccount> opt = userRepository.findByCheckNumber(checkNumber);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("user not found");
        }
        UserAccount user = opt.get();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return ResponseEntity.ok("password updated");
    }
}
