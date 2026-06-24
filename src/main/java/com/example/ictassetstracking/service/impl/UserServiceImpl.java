package com.example.ictassetstracking.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ictassetstracking.dto.UserCreateRequest;
import com.example.ictassetstracking.dto.UserDto;
import com.example.ictassetstracking.dto.UserRolesRequest;
import com.example.ictassetstracking.dto.UserUpdateRequest;
import com.example.ictassetstracking.entity.Role;
import com.example.ictassetstracking.entity.UserAccount;
import com.example.ictassetstracking.repository.EmployeeRepository;
import com.example.ictassetstracking.repository.UserRepository;
import com.example.ictassetstracking.service.UserService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDto> listAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserDto> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toDto);
    }

    @Override
    public Page<UserDto> findUsersByCheckNumber(Long checkNumber, Pageable pageable) {
        return userRepository.findByCheckNumber(checkNumber, pageable)
                .map(this::toDto);
    }

    @Override
    public Page<UserDto> findUsersByName(String searchTerm, Pageable pageable) {
        return userRepository.findByName(searchTerm, pageable)
                .map(this::toDto);
    }

    @Override
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Override
    public UserDto createUser(UserCreateRequest request) {
        Long checkNumber = request.getCheckNumber();
        if (request.getUsername() != null && request.getUsername().matches("\\d+")) {
            Long parsedCheckNumber = Long.valueOf(request.getUsername());
            if (checkNumber == null) {
                checkNumber = parsedCheckNumber;
                request.setCheckNumber(parsedCheckNumber);
            } else if (!request.getCheckNumber().equals(parsedCheckNumber)) {
                throw new IllegalArgumentException("Username and check number do not match");
            }
        }

        if (checkNumber == null) {
            throw new IllegalArgumentException("Check number is required for user creation");
        }

        if (!employeeRepository.existsByCheckNumber(checkNumber)) {
            throw new IllegalArgumentException("Employee not found for check number: " + checkNumber);
        }
        if (userRepository.existsByCheckNumber(checkNumber)) {
            throw new IllegalArgumentException("Check number already registered: " + checkNumber);
        }

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username already exists: " + request.getUsername());
            }
        }

        Set<Role> roles = request.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Set.of(Role.ROLE_USER);
        }

        UserAccount user = new UserAccount();
        user.setUsername(request.getUsername());
        user.setCheckNumber(checkNumber);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setRoles(new HashSet<>(roles));
        return toDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long id, UserUpdateRequest request) {
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }
        if (request.getRoles() != null) {
            user.setRoles(new HashSet<>(request.getRoles()));
        }

        return toDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDto setRoles(Long id, UserRolesRequest request) {
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        user.setRoles(new HashSet<>(request.getRoles()));
        return toDto(userRepository.save(user));
    }

    @Override
    public UserDto addRoles(Long id, UserRolesRequest request) {
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        Set<Role> existing = new HashSet<>(user.getRoles());
        existing.addAll(request.getRoles());
        user.setRoles(existing);
        return toDto(userRepository.save(user));
    }

    @Override
    public UserDto removeRoles(Long id, UserRolesRequest request) {
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        Set<Role> remaining = new HashSet<>(user.getRoles());
        remaining.removeAll(request.getRoles());
        user.setRoles(remaining);
        return toDto(userRepository.save(user));
    }

    @Override
    public UserDto activateUser(Long id) {
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        user.setEnabled(true);
        return toDto(userRepository.save(user));
    }

    @Override
    public UserDto deactivateUser(Long id) {
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        user.setEnabled(false);
        return toDto(userRepository.save(user));
    }

    private UserDto toDto(UserAccount user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEnabled(user.isEnabled());
        dto.setCheckNumber(user.getCheckNumber());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRoles(user.getRoles());
        return dto;
    }
}
