package org.sdn.razorpay_clone.merchant.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.common.entity.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "merchant_webhook_config", indexes = {
        @Index(name = "idx_webhook_config_merchant_id", columnList = "merchant_id,enabled")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantWebhookConfig extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    Merchant merchant;


    @Column(nullable = false, length = 500)
    String targetUrl;
    String webhookSecretHash;
    @Column(nullable = false)
    Boolean enabled = false;
    String eventTypes; // Comma Separated list of event types to subscribe to, e.g. "payment.captured,payment.failed"

}
