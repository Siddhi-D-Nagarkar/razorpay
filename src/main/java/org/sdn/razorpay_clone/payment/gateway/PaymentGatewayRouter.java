package org.sdn.razorpay_clone.payment.gateway;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.common.enums.PaymentMethod;
import org.sdn.razorpay_clone.payment.gateway.dto.PaymentRequest;
import org.sdn.razorpay_clone.payment.gateway.dto.PaymentResult;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class PaymentGatewayRouter {
    PaymentStrategyFactory paymentStrategyFactory;

    public PaymentResult initiate(PaymentRequest request) {
        PaymentStrategy adapter = paymentStrategyFactory.getPaymentStrategy(request.method());
        return adapter.initiate(request);
    }

    public PaymentResult capture(PaymentMethod method, UUID paymentId) {
        PaymentStrategy adapter = paymentStrategyFactory.getPaymentStrategy(method);
        return adapter.capture(paymentId);
    }
}
