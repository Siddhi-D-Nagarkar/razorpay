package org.sdn.razorpay_clone.payment.gateway.strategy;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.payment.gateway.PaymentStrategy;
import org.sdn.razorpay_clone.payment.gateway.dto.PaymentRequest;
import org.sdn.razorpay_clone.payment.gateway.dto.PaymentResult;
import org.sdn.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;
import org.sdn.razorpay_clone.vault.service.VaultService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor()
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CardPaymentStrategy implements PaymentStrategy {
    VaultService vaultService;

    @Override
    public PaymentResult initiate(PaymentRequest request) {
        // Logic to initiate card payment
        String token = request.methodDetails().get("token").toString();

        PaymentProcessorResponse response = vaultService.charge(request.paymentId(), token, request.amount(), request.methodDetails());

        return switch (response) {
            case PaymentProcessorResponse.Success success -> new PaymentResult.Success(success.bankReference());
            case PaymentProcessorResponse.Failure failure ->
                    new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
            case PaymentProcessorResponse.Pending pending -> new PaymentResult.Pending(pending.processorReference());
        };
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("CARD_REF");
    }
}
