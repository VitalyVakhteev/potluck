package com.picnic.potluck.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
		name = "fundraisers",
		indexes = {
				@Index(name = "idx_fundraisers_active_lat_lon", columnList = "active,lat,lon"),
				@Index(name = "idx_fundraisers_organizer", columnList = "organizer_id"),
				@Index(name = "idx_fundraisers_starts_at", columnList = "starts_at"),
				@Index(name = "idx_fundraisers_ends_at", columnList = "ends_at"),
				@Index(name = "idx_fundraisers_created_at_desc", columnList = "created_at DESC"),
				@Index(name = "idx_fundraisers_organizer_created_at_desc", columnList = "organizer_id,created_at DESC")
		}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "organizer")
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Fundraiser extends AuditedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@EqualsAndHashCode.Include
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(
			name = "organizer_id",
			nullable = false,
			updatable = false,
			foreignKey = @ForeignKey(name = "fk_organizer_user")
	)
	private User organizer;

	@Column(nullable = false)
	private boolean active;

	@Column(nullable = false)
	private boolean reward;

	@NotBlank
	@Column(nullable = false, length = 80)
	private String title;

	@Size(max = 500)
	@Column(length = 500)
	private String description;

	@Email
	@Size(max = 254)
	@Column(length = 254)
	private String email;

	@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
	@Column(name = "phone_number", length = 20)
	private String phone;

	@NotNull
	@Column(nullable = false)
	@DecimalMin("-90.0")
	@DecimalMax("90.0")
	private Double lat;

	@NotNull
	@Column(nullable = false)
	@DecimalMin("-180.0")
	@DecimalMax("180.0")
	private Double lon;

	@Column(name = "starts_at")
	private Instant starts_at;

	@Column(name = "ends_at")
	private Instant ends_at;
}
