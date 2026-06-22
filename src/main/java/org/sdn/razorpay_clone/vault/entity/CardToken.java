package org.sdn.razorpay_clone.vault.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.common.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "card_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false, unique = true, length = 50)
    String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vault_card_id", nullable = false)
    VaultCard vaultCard;


    @Column(nullable = false)
    UUID customer;
    @Column(nullable = false)
    UUID merchant;

    LocalDateTime revokedAt;

}
