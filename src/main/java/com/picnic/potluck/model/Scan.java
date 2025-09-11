package com.picnic.potluck.model;

import com.picnic.potluck.util.Source;
import com.picnic.potluck.util.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(
    name = "scans",
    indexes = {
        @Index(name = "idx_scans_created_at", columnList = "createdAt")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Scan extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @Size(max = 16)
    private Source source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @Size(max = 16)
    private Status status;

    @Column(nullable = false, unique = true, length=64)
    @Size(max = 64)
    private String idempotencyKey;
}
