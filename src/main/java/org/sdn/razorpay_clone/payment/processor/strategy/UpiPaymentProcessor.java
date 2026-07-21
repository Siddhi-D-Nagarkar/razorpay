package org.sdn.razorpay_clone.payment.processor.strategy;

import org.sdn.razorpay_clone.common.util.RandomizerUtil;
import org.sdn.razorpay_clone.payment.processor.PaymentProcessor;
import org.sdn.razorpay_clone.payment.processor.dto.PaymentProcessorRequest;
import org.sdn.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

@Component
public class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        String bankCode = request.methodDetails() != null ?
                request.methodDetails().get("BANK").toString() : null;

        // Simulate a failure scenario for a specific bank code
        String VPA_CODE_FAIL = "fail@okaxis";
        if (VPA_CODE_FAIL.equals(bankCode)) {
            return new PaymentProcessorResponse.Failure("UPI_REJECTED", "Payment failed due to bank code failure.");
        }


        String processorRef = "UPI_PROCESSOR_" + RandomizerUtil.randomBase64(16);
        String bankRef = "BANK_REF_" + RandomizerUtil.randomBase64(16);
        return new PaymentProcessorResponse.Pending(processorRef);
    }
}
