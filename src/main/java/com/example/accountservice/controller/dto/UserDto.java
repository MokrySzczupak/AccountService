package com.example.accountservice.controller.dto;

import com.example.accountservice.model.Role;
import com.example.accountservice.model.User;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private final Long id;
    private final String name;
    private final String lastname;
    private final String email;
    private final List<Role> roles;

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.roles = user.getRoles();
    }
}
