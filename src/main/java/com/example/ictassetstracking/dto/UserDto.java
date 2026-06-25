package com.example.ictassetstracking.dto;

import com.example.ictassetstracking.entity.Role;

import java.util.Set;

public class UserDto {
    private Long id;
    private String username;
    private boolean enabled;
    @com.fasterxml.jackson.annotation.JsonProperty("check_number")
    private Long checkNumber;
    private Set<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private String firstName;
    private String lastName;

    public Long getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(Long checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        String displayName = null;
        if (firstName != null && !firstName.isBlank()) {
            displayName = firstName.trim();
        }
        if (lastName != null && !lastName.isBlank()) {
            displayName = (displayName == null ? "" : displayName + " ") + lastName.trim();
        }
        if (displayName == null || displayName.isBlank()) {
            displayName = username;
        }
        if (checkNumber != null) {
            displayName = String.format("%s (%d)", displayName, checkNumber);
        }
        return displayName;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
