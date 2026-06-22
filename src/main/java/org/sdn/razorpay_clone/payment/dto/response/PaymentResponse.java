package org.sdn.razorpay_clone.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.sdn.razorpay_clone.common.entity.Money;
import org.sdn.razorpay_clone.common.enums.PaymentMethod;
import org.sdn.razorpay_clone.common.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentResponse(
        UUID id,
        UUID orderId,
        UUID merchantId,
        Money amount,
        PaymentStatus status,
        PaymentMethod method,
        Map<String, Object> methodDetails,
        String errorCode,
        String errorDescription,
        LocalDateTime capturedAt,
        LocalDateTime createdAt
) {
}
