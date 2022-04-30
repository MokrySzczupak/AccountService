package com.example.accountservice.controller.dto;

import com.example.accountservice.model.AccessOperation;
import lombok.Data;

@Data
public class ChangeAccessDto {
    private String user;
    private AccessOperation operation;
}
