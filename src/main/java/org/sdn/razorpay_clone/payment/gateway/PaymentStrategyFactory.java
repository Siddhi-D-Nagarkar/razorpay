package org.sdn.razorpay_clone.payment.gateway;

import lombok.RequiredArgsConstructor;
import org.sdn.razorpay_clone.common.enums.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentStrategyFactory {
    private final Map<PaymentMethod, PaymentStrategy> paymentStrategies;

    public PaymentStrategy getPaymentStrategy(PaymentMethod paymentMethod) {
        PaymentStrategy paymentStrategy = paymentStrategies.get(paymentMethod);

        if (paymentStrategy == null) {
            throw new IllegalArgumentException("No payment strategy found for method: " + paymentMethod);
        }

        return paymentStrategy;

    }
}
