package org.sdn.razorpay_clone.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.sdn.razorpay_clone.common.entity.Money;

import java.time.LocalDateTime;
import java.util.Map;

public record CreateOrderRequest(
        @NotNull(message = "Amount is required")
        Money amount,
        @Size(max = 100)
        String receipt, //it is like order id created at the merchant BE
        Map<String, Object> notes,
        LocalDateTime expiresAt
) {
}



