package com.example.ictassetstracking.dto;

import com.example.ictassetstracking.entity.Role;

import java.util.Set;

public class UserUpdateRequest {

    private String password;
    private Boolean enabled;
    private Set<Role> roles;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
