package org.sdn.razorpay_clone.payment.processor;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.sdn.razorpay_clone.common.enums.PaymentMethod;
import org.sdn.razorpay_clone.payment.processor.dto.PaymentProcessorRequest;
import org.sdn.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

@AllArgsConstructor
@Component
public class PaymentProcessorRouter {
    private final PaymentProcessorFactory paymentProcessorFactory;

    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        PaymentProcessor processor = paymentProcessorFactory.getPaymentProcessor(request.method());
        return processor.charge(request);
    }
}
