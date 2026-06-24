package org.sdn.razorpay_clone.payment.processor;

import org.sdn.razorpay_clone.payment.processor.dto.PaymentProcessorRequest;
import org.sdn.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;

import java.io.IOException;

public interface PaymentProcessor {
    PaymentProcessorResponse charge(PaymentProcessorRequest request);
}
