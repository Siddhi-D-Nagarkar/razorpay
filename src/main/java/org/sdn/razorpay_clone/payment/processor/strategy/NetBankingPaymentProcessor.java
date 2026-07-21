package org.sdn.razorpay_clone.payment.processor.strategy;

import org.sdn.razorpay_clone.common.util.RandomizerUtil;
import org.sdn.razorpay_clone.payment.processor.PaymentProcessor;
import org.sdn.razorpay_clone.payment.processor.dto.PaymentProcessorRequest;
import org.sdn.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NetBankingPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        String bankCode = request.methodDetails() != null ?
                request.methodDetails().get("BANK").toString() : null;

        // Simulate a failure scenario for a specific bank code
        String BANK_CODE_FAIL = "BANK_CODE_FAIL";
        if (BANK_CODE_FAIL.equals(bankCode)) {
            return new PaymentProcessorResponse.Failure("BANK_CODE_FAIL", "Payment failed due to bank code failure.");
        }


        String processorRef = "NET_PROCESSOR_" + RandomizerUtil.randomBase64(16);
        String redirectRef = "http://REDIRECT_BANK.com" + processorRef;
        return new PaymentProcessorResponse.Pending(processorRef);
    }
}
