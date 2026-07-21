package org.sdn.razorpay_clone.common.audit;

import lombok.RequiredArgsConstructor;
import org.sdn.razorpay_clone.merchant.security.MerchantContext;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {
    private final MerchantContext merchantContext;

    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            String keyId = merchantContext.getKeyId();

            if (keyId != null && !keyId.isBlank()) {
                return Optional.of(keyId);
            }


            if (merchantContext.getMerchantId() != null) {
                return Optional.of(merchantContext.getMerchantId().toString());
            }
        } catch (Exception e) {
        }

        return Optional.of("SYSTEM");
    }
}
