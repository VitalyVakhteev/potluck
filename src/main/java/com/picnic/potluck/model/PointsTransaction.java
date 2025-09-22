package com.picnic.potluck.model;

import com.picnic.potluck.util.Reason;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
		name = "points_transactions",
		indexes = {
				@Index(name = "idx_points_transactions_created_at", columnList = "created_at"),
				@Index(name = "idx_points_transactions_user_created_at", columnList = "user_id,created_at DESC"),
				@Index(name = "idx_points_transactions_fundraiser_created_at", columnList = "fundraiser_id,created_at DESC"),
				@Index(name = "idx_points_transactions_scan_created_at", columnList = "scan_id,created_at DESC")
		}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "fundraiser", "scan"})
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class PointsTransaction extends AuditedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@EqualsAndHashCode.Include
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(
			name = "user_id",
			nullable = false,
			foreignKey = @ForeignKey(name = "fk_points_transaction_user")
	)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(
			name = "fundraiser_id",
			nullable = false,
			foreignKey = @ForeignKey(name = "fk_points_transaction_fundraiser")
	)
	private Fundraiser fundraiser;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(
			name = "scan_id",
			nullable = false,
			foreignKey = @ForeignKey(name = "fk_points_transaction_scan")
	)
	private Scan scan;

	@Column(nullable = false)
	@Min(-100000)
	@Max(100000)
	// Sensible min/max for now; might change later
	private int delta;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private Reason reason;
}
