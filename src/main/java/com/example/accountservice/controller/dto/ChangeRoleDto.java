package com.example.accountservice.controller.dto;

import com.example.accountservice.model.ChangeRoleOperation;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ChangeRoleDto {
    @NotEmpty
    private String user;
    private String role;
    private ChangeRoleOperation operation;
}
