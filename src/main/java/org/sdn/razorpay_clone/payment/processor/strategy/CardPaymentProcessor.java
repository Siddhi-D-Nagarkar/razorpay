package org.sdn.razorpay_clone.payment.processor.strategy;

import org.sdn.razorpay_clone.payment.processor.PaymentProcessor;
import org.sdn.razorpay_clone.payment.processor.dto.PaymentProcessorRequest;
import org.sdn.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;

public class CardPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        return null;
    }
}
