package com.govnet.govnet.enums;

public enum Role {
    ROLE_ADMIN("Admin"),
    ROLE_USER("User");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
