package com.picnic.potluck.model;

import com.picnic.potluck.util.Reason;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(
        name = "points_transactions",
        indexes = {
                @Index(name = "idx_points_transactions_created_at", columnList = "createdAt")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class PointsLedger extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private int id;

    @Column(nullable = false, unique = true, length=12)
    @Size(max = 12)
    private int delta;

    @Column(nullable = false, length=16)
    @Size(max = 16)
    @Enumerated(EnumType.STRING)
    private Reason reason;
}
