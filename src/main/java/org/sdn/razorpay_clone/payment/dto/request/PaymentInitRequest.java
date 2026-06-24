package org.sdn.razorpay_clone.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import org.sdn.razorpay_clone.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentInitRequest(
        @NotNull(message = "orderId cannot be null")
        UUID orderId,
        @NotNull(message = "method cannot be null")
        PaymentMethod method,
        Map<String, Object> methodDetails
) {
}
