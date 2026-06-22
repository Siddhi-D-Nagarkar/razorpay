package org.sdn.razorpay_clone.operations.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.common.entity.BaseEntity;

@Entity
@Table(name = "settlement_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SettlementPayment extends BaseEntity {

    @EmbeddedId
    SettlementPaymentId id;

    @MapsId("settlementId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "settlement_id",nullable = false)
    Settlement settlement;
}
