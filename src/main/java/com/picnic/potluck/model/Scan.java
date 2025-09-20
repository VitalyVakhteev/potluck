package com.picnic.potluck.model;

import com.picnic.potluck.util.Source;
import com.picnic.potluck.util.Status;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(
    name = "scans",
    uniqueConstraints = {
            @UniqueConstraint(name = "uk_scans_idempotency_key", columnNames = "idempotency_key"),
            @UniqueConstraint(name = "uk_scans_same", columnNames={"participant","fundraiser"})
    },
    indexes = {
            @Index(name = "idx_scans_participant_created_at", columnList = "participant_user_id,created_at DESC")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"fundraiser", "participant", "organizer"})
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Scan extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "fundraiser_id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_scans_fundraiser")
    )
    private Fundraiser fundraiser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "participant_user_id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_scans_participant_user")
    )
    private User participant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "organizer_user_id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_scans_organizer_user")
    )
    private User organizer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Source source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    @Column(name = "idempotency_key", nullable = false, length = 64)
    private String idempotencyKey;
}
