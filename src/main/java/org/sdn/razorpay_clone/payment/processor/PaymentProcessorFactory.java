package org.sdn.razorpay_clone.payment.processor;


import lombok.RequiredArgsConstructor;
import org.sdn.razorpay_clone.common.enums.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class PaymentProcessorFactory {
    private final Map<PaymentMethod, PaymentProcessor> paymentProcessorMap;

    public PaymentProcessor getPaymentProcessor(PaymentMethod paymentMethod) {
        PaymentProcessor paymentProcessor = paymentProcessorMap.get(paymentMethod);
        if (paymentProcessor == null) {
            throw new IllegalArgumentException("No payment processor registered for method: " + paymentMethod);
        }

        return paymentProcessor;
    }

}
