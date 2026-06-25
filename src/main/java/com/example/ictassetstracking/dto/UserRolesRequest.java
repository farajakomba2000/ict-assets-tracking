package com.example.ictassetstracking.dto;

import com.example.ictassetstracking.entity.Role;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public class UserRolesRequest {

    @NotEmpty
    private Set<Role> roles;

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
