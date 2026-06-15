package org.sdn.razorpay_clone.payment.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.merchant.entity.Merchant;

import java.util.UUID;

@Entity
@Table(name = "merchant_webhook_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantWebhookConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "merchant_id",nullable = false)
    Merchant merchant;


    @Column(nullable = false, length = 500)
    String targetUrl;
    String webhookSecretHash;
    @Column(nullable = false)
    Boolean enabled = false;
    String eventTypes; // Comma Separated list of event types to subscribe to, e.g. "payment.captured,payment.failed"

}
