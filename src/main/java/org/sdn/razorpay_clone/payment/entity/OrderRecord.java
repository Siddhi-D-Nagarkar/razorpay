package org.sdn.razorpay_clone.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.sdn.razorpay_clone.common.entity.Money;
import org.sdn.razorpay_clone.common.enums.OrderStatus;
import org.sdn.razorpay_clone.merchant.entity.Merchant;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "merchant_id", nullable = false)
    UUID merchantId;

    @Embedded
    Money amount;
    @Enumerated(EnumType.STRING)
    OrderStatus status = OrderStatus.CREATED;
    @Column(nullable = false)
    Integer attempts;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    Map<String, Object> notes;
    @Column(nullable = false)
    LocalDateTime expiresAt;
}
