package org.sdn.razorpay_clone.merchant.dto.request;

import org.sdn.razorpay_clone.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
