package com.picnic.potluck.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    private int id;

    @Column(name = "role")
    private Enum role; // Todo: add custom enum for ADMIN, USER

    @Column(name = "is_active")
    private boolean is_active;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String password_hash;

    @Column(name = "bio")
    private String bio;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phone_number;

    @Column(name = "created_at")
    private String created_at; // Todo: Change to timestamp object

    @Column(name = "updated_at")
    private String updated_at; // Todo: Change to timestamp object

    public User() {
        // Constructor that initializes fields to default values
    }
}
