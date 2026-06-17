package org.sdn.razorpay_clone.merchant.dto.response;

import lombok.Builder;
import org.sdn.razorpay_clone.common.enums.Environment;

import java.time.LocalDateTime;
import java.util.UUID;
@Builder
public record ApiKeyResponse(
        UUID id,
        String keyId,
        Environment environment,
        Boolean enabled,
        LocalDateTime lastUsedAt,
        LocalDateTime createdAt
) {
}
