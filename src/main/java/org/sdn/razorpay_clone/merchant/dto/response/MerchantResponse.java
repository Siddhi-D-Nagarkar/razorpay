package org.sdn.razorpay_clone.merchant.dto.response;

import lombok.Builder;
import org.sdn.razorpay_clone.common.enums.BusinessType;
import org.sdn.razorpay_clone.common.enums.MerchantStatus;
import org.sdn.razorpay_clone.merchant.entity.Merchant;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link Merchant}
 */
@Builder
public record MerchantResponse(UUID id, String name, String email, BusinessType businessType,
                               String businessName, MerchantStatus status) {
}