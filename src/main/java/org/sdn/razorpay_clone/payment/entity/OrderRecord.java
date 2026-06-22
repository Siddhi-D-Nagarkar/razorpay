package org.sdn.razorpay_clone.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.sdn.razorpay_clone.common.entity.BaseEntity;
import org.sdn.razorpay_clone.common.entity.Money;
import org.sdn.razorpay_clone.common.enums.OrderStatus;
import org.sdn.razorpay_clone.merchant.entity.Merchant;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_record", indexes = {
        @Index(name = "idx_order_id_merchant_id", columnList = "id,merchant_id"),
        @Index(name = "idx_order_merchant_id", columnList = "merchant_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRecord extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "merchant_id", nullable = false)
    UUID merchantId;

    @Embedded
    Money amount;

    @Column(length = 100)
    String receipt; //it is like order id created at the merchant BE
    @Enumerated(EnumType.STRING)
    OrderStatus status = OrderStatus.CREATED;
    @Column(nullable = false)
    @Builder.Default
    Integer attempts = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    Map<String, Object> notes;
    @Column(nullable = false)
    LocalDateTime expiresAt;
}
