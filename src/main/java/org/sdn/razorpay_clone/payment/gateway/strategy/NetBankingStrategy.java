package org.sdn.razorpay_clone.payment.gateway.strategy;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.sdn.razorpay_clone.common.enums.PaymentMethod;
import org.sdn.razorpay_clone.payment.gateway.PaymentStrategy;
import org.sdn.razorpay_clone.payment.gateway.dto.PaymentRequest;
import org.sdn.razorpay_clone.payment.gateway.dto.PaymentResult;
import org.sdn.razorpay_clone.payment.processor.PaymentProcessor;
import org.sdn.razorpay_clone.payment.processor.PaymentProcessorFactory;
import org.sdn.razorpay_clone.payment.processor.PaymentProcessorRouter;
import org.sdn.razorpay_clone.payment.processor.dto.PaymentProcessorRequest;
import org.sdn.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NetBankingStrategy implements PaymentStrategy {
    PaymentProcessorRouter paymentProcessorRouter;


    @Override
    public PaymentResult initiate(PaymentRequest request) {
        // Implement the logic to initiate a net banking payment
        log.info("Initiating net banking payment for request with paymentId : {}", request.paymentId());
        try {
            PaymentProcessorRequest paymentProcessorRequest =
                    PaymentProcessorRequest.nonCard(request.paymentId(), PaymentMethod.NETBANKING, request.amount(), request.methodDetails());
            PaymentProcessorResponse paymentProcessorResponse = paymentProcessorRouter.charge(paymentProcessorRequest);

            switch (paymentProcessorResponse) {
                case PaymentProcessorResponse.Failure failure -> {
                    return new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
                }
                case PaymentProcessorResponse.Pending pending -> {
                    return new PaymentResult.Pending(pending.processorReference());
                }
                case PaymentProcessorResponse.Success success -> {
                    return new PaymentResult.Success(success.processorReference());
                }
            }
        } catch (Exception e) {
            log.warn("Exception occurred while initiating net banking payment for request with paymentId : {}. Exception : {}",
                    request.paymentId(), e.getMessage());
            return new PaymentResult.Failure("NBK_FAILED", "An internal error occurred while processing the payment.");
        }

    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("NBK_REF");
    }
}
