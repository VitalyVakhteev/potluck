package com.picnic.potluck.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_followers",
        uniqueConstraints = @UniqueConstraint(name="pk_user_follow", columnNames={"user_id","follower_user_id"}),
        indexes = {
                @Index(name="idx_follow_user", columnList="user_id"),
                @Index(name="idx_follow_follower", columnList="follower_user_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFollow extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable=false, foreignKey=@ForeignKey(name="fk_follow_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="follower_user_id", nullable=false, foreignKey=@ForeignKey(name="fk_follow_follower"))
    private User follower;
}