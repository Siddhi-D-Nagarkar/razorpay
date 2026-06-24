package org.sdn.razorpay_clone.payment.gateway;

import org.sdn.razorpay_clone.payment.gateway.dto.PaymentRequest;
import org.sdn.razorpay_clone.payment.gateway.dto.PaymentResult;

public interface PaymentStrategy {
    PaymentResult initiate(PaymentRequest request);
}
