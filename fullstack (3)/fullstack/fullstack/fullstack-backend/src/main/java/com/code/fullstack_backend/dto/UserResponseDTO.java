package com.code.fullstack_backend.dto;

import java.time.LocalDateTime;

public class UserResponseDTO {

    private Long id;
    private String username;
    private String name;
    private String email;
    private String address;
    private LocalDateTime lastEdited;
    private String role;

    private String weatherInfo; // New field


    // Getters and Setters

    public UserResponseDTO() {
    }

    public UserResponseDTO(Long id, String username, String name, String email, String address, LocalDateTime lastEdited,String weatherInfo) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.address = address;
        this.lastEdited=lastEdited;
        this.weatherInfo=weatherInfo;
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public String getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(String weatherInfo) {
        this.weatherInfo = weatherInfo;
    }

    public LocalDateTime getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(LocalDateTime lastEdited) {
        this.lastEdited = lastEdited;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
