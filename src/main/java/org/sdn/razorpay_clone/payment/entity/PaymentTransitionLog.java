package org.sdn.razorpay_clone.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.common.enums.PaymentActor;
import org.sdn.razorpay_clone.common.enums.PaymentEvent;
import org.sdn.razorpay_clone.common.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_transition_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentTransitionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    PaymentStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    PaymentEvent event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    PaymentStatus toStatus;

    @Column(length = 100)
    @Enumerated(EnumType.STRING)
    PaymentActor actor;
    @Column(nullable = false)
    LocalDateTime occurredAt;

}
