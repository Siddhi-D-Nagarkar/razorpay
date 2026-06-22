package org.sdn.razorpay_clone.operations.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.sdn.razorpay_clone.common.entity.BaseEntity;
import org.sdn.razorpay_clone.common.enums.WebhookEventStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "webhook_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebhookEvent extends BaseEntity { // For Logging Purpose this is the table
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false)
    UUID merchantId;
    @Column(nullable = false, length = 40)
    String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    Map<String, Objects> payload; // Store the entire event payload as JSON in the database

    @Column(nullable = false)
    String targetUrl;

    @Column(nullable = false)
    String signature;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    WebhookEventStatus status;

    @Column(nullable = false)
    Integer attempts = 0;
    LocalDateTime nextRetryAt;
    LocalDateTime lastAttemptAt;
    Integer lastResponseCode;

    @Column(length = 1000)
    String lastResponseBody;
    LocalDateTime deliveredAt;

}
