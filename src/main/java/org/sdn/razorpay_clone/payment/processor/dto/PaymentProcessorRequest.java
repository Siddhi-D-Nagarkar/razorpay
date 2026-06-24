package org.sdn.razorpay_clone.payment.processor.dto;

import org.sdn.razorpay_clone.common.entity.Money;
import org.sdn.razorpay_clone.common.enums.PaymentMethod;

import java.util.Map;

public record PaymentProcessorRequest(
        PaymentMethod method,
        Money amount,
        Map<String, Object> methodDetails
) {
}
