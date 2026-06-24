package org.sdn.razorpay_clone.payment.gateway.dto;

import lombok.Builder;
import org.sdn.razorpay_clone.common.entity.Money;
import org.sdn.razorpay_clone.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

@Builder
public record PaymentRequest(
    UUID paymentId,
    UUID orderId,
    UUID merchantId,
    Money amount,
    PaymentMethod method,
    Map<String, Object> methodDetails
) {
}
