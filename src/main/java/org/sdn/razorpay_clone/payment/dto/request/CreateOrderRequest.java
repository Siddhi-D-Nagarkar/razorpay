package org.sdn.razorpay_clone.payment.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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
        LocalDateTime expiresAt,
        @Valid
        CustomerDetails customer
) {

    public record CustomerDetails(
            @Size(max = 200)
            String name,

            @Email
            @Size(max = 200)
            String email,

            @Size(max = 20)
            String phone
    ) {
    }
}



