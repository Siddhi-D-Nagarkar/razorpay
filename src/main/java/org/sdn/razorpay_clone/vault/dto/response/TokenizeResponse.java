package org.sdn.razorpay_clone.vault.dto.response;

import lombok.Builder;
import org.sdn.razorpay_clone.common.enums.CardBrand;

@Builder
public record TokenizeResponse(
        String token,
        String lastFour,
        CardBrand brand,
        Integer expiryMonth,
        Integer expiryYear
) {
}
