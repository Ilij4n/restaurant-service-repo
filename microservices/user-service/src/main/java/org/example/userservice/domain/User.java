package org.example.userservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_username", columnList = "username"),
        @Index(name = "idx_users_role", columnList = "role")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "is_active")
    private Boolean isActive = false;

    @Column(name = "is_banned")
    private Boolean isBanned = false;

    // Ovo polje se automatski sinhronizuje sa dikriminator kolonom pomocu anotacija ispod
    @Enumerated(EnumType.STRING)
    @Column(name = "role", insertable = false, updatable = false)
    private Role role;

    // This method ensures role is set based on the actual entity type
    @PostLoad
    @PostPersist
    private void setupRole() {
        if (this instanceof Admin) {
            this.role = Role.ADMIN;
        } else if (this instanceof Client) {
            this.role = Role.CLIENT;
        } else if (this instanceof RestaurantManager) {
            this.role = Role.MANAGER;
        }
    }
}