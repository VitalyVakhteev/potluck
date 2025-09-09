package com.picnic.potluck.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.picnic.potluck.util.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uc_users_username", columnNames = "username"),
        @UniqueConstraint(name = "uc_users_email", columnNames = "email")
    },
    indexes = {
        @Index(name = "idx_users_active", columnList = "active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "passwordHash")
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class User extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Role role;

    @Column(nullable = false)
    private boolean active;

    @NotBlank
    @Size(min = 3, max = 40)
    @Column(nullable = false, length = 40)
    private String username;

    @JsonIgnore
    @Column(name = "password_hash", length = 100)
    private String passwordHash;

    @Size(max = 160)
    private String bio;

    @Email
    @Size(max = 254)
    @Column(unique = true)
    private String email;

    // Note: we'll have to strip away any dashes inputted by users on the frontend
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    @Column(name = "phone_number", length = 20)
    private String phone_number;
}
