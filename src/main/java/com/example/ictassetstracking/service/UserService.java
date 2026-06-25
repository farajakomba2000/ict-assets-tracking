package com.example.ictassetstracking.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.ictassetstracking.dto.UserCreateRequest;
import com.example.ictassetstracking.dto.UserDto;
import com.example.ictassetstracking.dto.UserRolesRequest;
import com.example.ictassetstracking.dto.UserUpdateRequest;

public interface UserService {

    List<UserDto> listAllUsers();

    Page<UserDto> listUsers(Pageable pageable);

    Page<UserDto> findUsersByCheckNumber(Long checkNumber, Pageable pageable);

    Page<UserDto> findUsersByName(String searchTerm, Pageable pageable);

    UserDto getUserById(Long id);

    UserDto createUser(UserCreateRequest request);

    UserDto updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    UserDto setRoles(Long id, UserRolesRequest request);

    UserDto addRoles(Long id, UserRolesRequest request);

    UserDto removeRoles(Long id, UserRolesRequest request);

    UserDto activateUser(Long id);

    UserDto deactivateUser(Long id);
}
