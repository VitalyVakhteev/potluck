package com.picnic.potluck.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "user_favorite_fundraisers",
    uniqueConstraints = {
            @UniqueConstraint(name="pk_user_fav", columnNames={"user_id","fundraiser_id"})
    },
    indexes = {
            @Index(name="idx_user_fav_user", columnList="user_id"),
            @Index(name="idx_user_fav_fundraiser", columnList="fundraiser_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFavoriteFundraiser extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable=false, foreignKey=@ForeignKey(name="fk_fav_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="fundraiser_id", nullable=false, foreignKey=@ForeignKey(name="fk_fav_fundraiser"))
    private Fundraiser fundraiser;
}