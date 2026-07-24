package org.sdn.razorpay_clone.merchant.cache;

import lombok.Builder;
import org.sdn.razorpay_clone.common.enums.Environment;

import java.time.LocalDateTime;
import java.util.UUID;


@Builder
public record ApiKeyCacheEntry(
        UUID merchantId,
        String keyId,
        String keySecretHash,
        String previousKeySecretHash,
        Environment environment,
        LocalDateTime gracePeriodExpiryAt,
        Boolean enabled
) {
    public boolean isInGracePeriod() {
        return gracePeriodExpiryAt != null &&
                LocalDateTime.now().isBefore(gracePeriodExpiryAt);
    }
}
