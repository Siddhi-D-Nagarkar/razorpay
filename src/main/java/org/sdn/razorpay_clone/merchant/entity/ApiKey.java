package org.sdn.razorpay_clone.merchant.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.common.entity.BaseEntity;
import org.sdn.razorpay_clone.common.enums.Environment;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(indexes = {
        @Index(name = "idx_api_key_merchant_id",columnList = "merchant_id"),
        @Index(name = "idx_api_key_merchant_env",columnList = "merchant_id,environment,enabled")
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiKey extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    Merchant merchant;

    @Column(nullable = false, unique = true, length = 50)
    String keyId;

    @Column(nullable = false, length = 200)
    String keySecretHash;

    @Column(length = 200)
    String previousKeySecretHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    Environment environment;

    @Column(nullable = false)
    @Builder.Default
    Boolean enabled = true;
    LocalDateTime lastUsedAt;
    LocalDateTime rotatedAt;
    LocalDateTime gracePeriodExpiryAt;
}
