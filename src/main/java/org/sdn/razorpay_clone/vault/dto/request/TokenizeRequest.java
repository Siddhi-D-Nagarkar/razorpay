package org.sdn.razorpay_clone.vault.dto.request;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.LuhnCheck;
import org.sdn.razorpay_clone.vault.validation.ExpiryYear;

import java.util.UUID;

public record TokenizeRequest(
        @NotBlank(message = "PAN is required")
        @LuhnCheck(message = "PAN is invalid")
        @Pattern(regexp = "^[0-9]{13,19}$", message = "PAN must be between 13 and 19 digits")
        String pan,

        @NotBlank(message = "CVV is required")
        @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be 3 or 4 digits")
        String cvv,

        @NotNull(message = "Expiry month is required")
        @Min(value = 1, message = "Expiry month must be between 01 and 12")
        @Max(value = 12, message = "Expiry month must be between 01 and 12")
        Integer expiryMonth,

        @NotNull(message = "Expiry year is required")
        @Min(value = 2026, message = "Expiry year must be greater than or equal to 2026")
        @ExpiryYear(message = "Expiry year cannot be in the past")
        Integer expiryYear,
        UUID customerId,
        @Size(min = 3, message = "Card holder name must be at least 3 characters long")
        String cardHolderName
) {
}
