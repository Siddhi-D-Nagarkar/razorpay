package org.sdn.razorpay_clone.payment.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.common.enums.PaymentMethod;
import org.sdn.razorpay_clone.payment.processor.PaymentProcessor;
import org.sdn.razorpay_clone.payment.processor.strategy.CardPaymentProcessor;
import org.sdn.razorpay_clone.payment.processor.strategy.NetBankingPaymentProcessor;
import org.sdn.razorpay_clone.payment.processor.strategy.UpiPaymentProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentProcessorConfig {
    CardPaymentProcessor cardPaymentProcessor;
    NetBankingPaymentProcessor netBankingPaymentProcessor;
    UpiPaymentProcessor upiPaymentProcessor;

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap() {
        return Map.of(
                PaymentMethod.CARD, this.cardPaymentProcessor,
                PaymentMethod.NETBANKING, this.netBankingPaymentProcessor,
                PaymentMethod.UPI, this.upiPaymentProcessor
        );
    }
}
