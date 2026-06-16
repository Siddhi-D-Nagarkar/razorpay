package org.sdn.razorpay_clone.merchant.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.sdn.razorpay_clone.common.enums.BusinessType;

import java.io.Serializable;

/**
 * DTO for {@link org.sdn.razorpay_clone.merchant.entity.Merchant}
 */
public record MerchantSignUpRequest(
        @NotNull(message = "Name should be provided")
        @Size(max = 50, message = "Name should not exceed 50 characters")
        String name,
        @Email(message = "Email should be valid")
        @NotNull(message = "Email should be provided")
        String email,
        @NotNull(message = "Password should be provided")
        @Size(min = 8, message = "Password should be at least 8 characters long")
        String password,

        BusinessType businessType,
        @Size(max = 50, message = "Business name should not exceed 50 characters")
        String businessName) {
}