package org.example.security.domain;

public enum Role {
    ADMIN,
    CLIENT,
    MANAGER;

    // Enum konverzije su case-sensitive
    // Eto mi ga nisam hteo lepo da radim sa stringovima...
    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }
}