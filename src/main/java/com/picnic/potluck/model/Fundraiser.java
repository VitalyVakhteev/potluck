package com.picnic.potluck.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "fundraisers",
        indexes = {
                @Index(name = "idx_fundraisers_active_lat_lon", columnList = "active,lat,lon")
                @Index(name = "idx_fundraisers_organizer", columnList = "organizer_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
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
    private User organizer_user;

    @Column(nullable = false)
    private boolean active;

    @Size(max = 32)
    private String title;

    @Size(max = 160)
    private String description;

    @Email
    @Size(max = 254)
    @Column(length = 254)
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    @Column(name = "phone_number", length = 20)
    private String phone_number;
}
