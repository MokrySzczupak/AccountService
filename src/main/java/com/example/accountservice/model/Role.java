package com.example.accountservice.model;

public enum Role {
    ROLE_ADMINISTRATOR,
    ROLE_ACCOUNTANT,
    ROLE_USER,
    ROLE_AUDITOR;

    public boolean isBusiness() {
        return this.equals(ROLE_ACCOUNTANT) ||
                this.equals(ROLE_AUDITOR) ||
                this.equals(ROLE_USER);
    }

    public boolean isAdmin() {
        return this.equals(ROLE_ADMINISTRATOR);
    }
}
