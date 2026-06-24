package org.sdn.razorpay_clone.payment.gateway.strategy;

import org.sdn.razorpay_clone.payment.gateway.PaymentStrategy;
import org.sdn.razorpay_clone.payment.gateway.dto.PaymentRequest;
import org.sdn.razorpay_clone.payment.gateway.dto.PaymentResult;
import org.springframework.stereotype.Component;

@Component
public class CardPaymentStrategy implements PaymentStrategy {
    @Override
    public PaymentResult initiate(PaymentRequest request) {
        // Logic to initiate card payment
        return null;
    }
}
