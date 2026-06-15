package org.sdn.razorpay_clone.operations.entity;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class SettlementPaymentId {
    UUID settlementId;
    UUID paymentId;
}
