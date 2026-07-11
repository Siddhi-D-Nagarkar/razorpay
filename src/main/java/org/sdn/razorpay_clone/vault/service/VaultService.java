package org.sdn.razorpay_clone.vault.service;

import org.sdn.razorpay_clone.common.entity.Money;
import org.sdn.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;
import org.sdn.razorpay_clone.vault.dto.request.TokenizeRequest;
import org.sdn.razorpay_clone.vault.dto.response.TokenizeResponse;

import java.util.Map;
import java.util.UUID;

public interface VaultService {
    TokenizeResponse tokenize(TokenizeRequest tokenizeRequest, UUID merchantId);

    PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails);
}
