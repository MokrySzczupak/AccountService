package com.example.accountservice.model;

public enum ChangeRoleOperation {
    GRANT, REMOVE;

    public String getFormattedName() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
