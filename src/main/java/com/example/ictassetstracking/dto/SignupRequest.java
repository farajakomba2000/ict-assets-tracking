package com.example.ictassetstracking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SignupRequest {
    @NotNull
    @JsonProperty("check_number")
    private Long checkNumber;

    @NotBlank
    private String password;

    public Long getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(Long checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getUsername() {
        return checkNumber != null ? String.valueOf(checkNumber) : null;
    }

    public void setUsername(String username) {
        if (username != null && !username.isBlank()) {
            this.checkNumber = Long.valueOf(username);
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
