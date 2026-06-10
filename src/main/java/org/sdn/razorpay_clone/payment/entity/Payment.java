package org.sdn.razorpay_clone.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.sdn.razorpay_clone.common.entity.Money;
import org.sdn.razorpay_clone.common.enums.PaymentMethod;
import org.sdn.razorpay_clone.common.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Embedded
    Money amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    OrderRecord order;

    @Column(nullable = false)
    UUID merchantId;

    @Column(nullable = false, length = 100)
    String idempotencyKey;
    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PaymentMethod method;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "method_details")
    Map<String, Object> methodDetails;
    @Column(length = 100)
    String bankReference;
    @Column(length = 100)
    String errorCode;
    @Column(length = 200)
    String errorDescription;
    LocalDateTime authorizedAt;
    LocalDateTime capturedAt;
    LocalDateTime failedAt;
    LocalDateTime refundedAt;
    LocalDateTime settledAt;

}
