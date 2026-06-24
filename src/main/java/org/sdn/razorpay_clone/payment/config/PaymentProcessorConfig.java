package org.sdn.razorpay_clone.payment.config;

import org.sdn.razorpay_clone.common.enums.PaymentMethod;
import org.sdn.razorpay_clone.payment.processor.PaymentProcessor;
import org.sdn.razorpay_clone.payment.processor.strategy.CardPaymentProcessor;
import org.sdn.razorpay_clone.payment.processor.strategy.NetBankingPaymentProcessor;
import org.sdn.razorpay_clone.payment.processor.strategy.UpiPaymentProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentProcessorConfig {

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap() {
        return Map.of(
                PaymentMethod.CARD, new CardPaymentProcessor(),
                PaymentMethod.NETBANKING, new NetBankingPaymentProcessor(),
                PaymentMethod.UPI, new UpiPaymentProcessor()
        );
    }
}
