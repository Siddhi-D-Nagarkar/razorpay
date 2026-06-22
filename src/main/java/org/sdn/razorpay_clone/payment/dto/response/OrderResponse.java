package org.sdn.razorpay_clone.payment.dto.response;

import lombok.Builder;
import org.sdn.razorpay_clone.common.entity.Money;
import org.sdn.razorpay_clone.common.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Builder
public record OrderResponse(
        UUID id,
        UUID merchantId,
        String receipt,
        Money amount,
        OrderStatus status,
        Integer attempts,
        Map<String, Object> notes,
        LocalDateTime expiresAt,
        LocalDateTime createdAt
) {
}
