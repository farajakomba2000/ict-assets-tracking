package com.example.ictassetstracking.dto;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    @com.fasterxml.jackson.annotation.JsonProperty("check_number")
    private Long checkNumber;

    public JwtResponse(String token, Long checkNumber) {
        this.token = token;
        this.checkNumber = checkNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(Long checkNumber) {
        this.checkNumber = checkNumber;
    }
}
