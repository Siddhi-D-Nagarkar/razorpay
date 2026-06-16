package org.sdn.razorpay_clone.merchant.dto.response;

import lombok.Builder;
import org.sdn.razorpay_clone.common.enums.Environment;

import java.util.UUID;

@Builder
public record ApiKeyCreateResponse(
        UUID id,
        String keyId,
        String keySecret,
        Environment environment
) {
}
