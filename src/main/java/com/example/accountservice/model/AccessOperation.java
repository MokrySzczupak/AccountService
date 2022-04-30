package com.example.accountservice.model;

public enum AccessOperation {
    LOCK, UNLOCK;

    public String getFormattedName() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
