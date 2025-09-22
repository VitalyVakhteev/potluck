package com.picnic.potluck.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.picnic.potluck.util.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
		name = "users",
		uniqueConstraints = {
				@UniqueConstraint(name = "uc_users_username", columnNames = "username"),
				@UniqueConstraint(name = "uc_users_email", columnNames = "email"),
				@UniqueConstraint(name = "uc_users_phone", columnNames = "phone_number")
		},
		indexes = {
				@Index(name = "idx_users_active", columnList = "active"),
				@Index(name = "idx_users_total_points_desc", columnList = "total_points DESC")
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
	private UUID id;

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

	@Column(name = "first_name", length = 50)
	private String firstName;

	@Column(name = "last_name", length = 50)
	private String lastName;

	@Size(max = 160)
	private String bio;

	@Email
	@Size(max = 254)
	@Column(length = 254)
	private String email;

	@Column(name = "total_points")
	private int totalPoints;

	@Column(name = "total_fundraisers")
	private int totalFundraisers;

	// Note that this isn't a lat/lon object, rather a str location
	@Size(max = 120)
	@Column(name = "location", length = 120)
	private String location;

	@Pattern(regexp = "^[0-9()\\s+\\-.]{7,32}$")
	@Column(name = "phone_number", length = 32)
	private String phoneNumber;

	@Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")
	@Column(name = "banner_color", length = 7)
	private String bannerColor;

	@Column(name = "display_name", nullable = false)
	private boolean displayName;

	@Column(name = "display_email", nullable = false)
	private boolean displayEmail;

	@Column(name = "display_phone", nullable = false)
	private boolean displayPhone;
}
