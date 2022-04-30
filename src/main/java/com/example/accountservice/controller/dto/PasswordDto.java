package com.example.accountservice.controller.dto;

public class PasswordDto {
    private String new_password;

    public PasswordDto() {}

    public void setNew_password(String password) {
        this.new_password = password;
    }

    public String getPassword() {
        return new_password;
    }
}
