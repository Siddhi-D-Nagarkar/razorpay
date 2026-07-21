package org.sdn.razorpay_clone.payment.stimulator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdn.razorpay_clone.common.enums.ChaosMode;
import org.sdn.razorpay_clone.common.enums.PaymentStatus;
import org.sdn.razorpay_clone.common.util.RandomizerUtil;
import org.sdn.razorpay_clone.payment.entity.Payment;
import org.sdn.razorpay_clone.payment.repository.PaymentRepository;
import org.sdn.razorpay_clone.payment.service.PaymentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BankCallbackSimulator {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SimulatorConfig simulatorConfig;

//    @Scheduled(fixedDelayString = "${payment.simulator.poll-interval-ms:5000}")
    public void processCallbacks() {
        log.info("Chaos Mode {}",this.simulatorConfig.getChaosMode());

        LocalDateTime globalWindow = LocalDateTime.now().minusSeconds(1);

        List<Payment> candidates = paymentRepository
                .findByPaymentStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING, globalWindow);
        log.info("Found {} payments in AUTHORIZING state for simulation", candidates.size());
        if (candidates.isEmpty()) return;

        for (Payment payment : candidates) {
            simulateCallback(payment);
        }

    }

    private void simulateCallback(Payment payment) {
        SimulatorConfig.MethodSimulatorConfig methodConfig = simulatorConfig.configFor(payment.getMethod());
        LocalDateTime dueAt = this.dueAt(payment, methodConfig);

        if (LocalDateTime.now().isBefore(dueAt)) {
            return;
        }

        switch (simulatorConfig.getChaosMode()) {
            case SUCCESS -> {
                this.resolve(payment, true);
            }
            case FAILURE -> {
                this.resolve(payment, false);
            }
            case TIMEOUT -> {
                log.debug("Payment {} is still in AUTHORIZING state due to TIMEOUT chaos mode", payment.getId());
            }
            case NORMAL, SLOW -> {
                this.resolve(payment, this.shouldApprove(payment, methodConfig));
            }
        }

    }

    private void resolve(Payment payment, boolean approve) {
        if (approve) {
            String bankRef = "SIM_BANK_REF_" + RandomizerUtil.randomBase64(8);
            paymentService.resolveAuthorization(payment.getId(), true, bankRef, null, null);
        } else {
            paymentService.resolveAuthorization(payment.getId(), false, null, "SIM_BANK_ERROR_CODE", "Simulated bank error message");
        }
    }

    private Boolean shouldApprove(Payment payment, SimulatorConfig.MethodSimulatorConfig methodConfig) {
        int bucket = Math.abs(payment.getId().hashCode()) % 100;
        return bucket < methodConfig.getSuccessRate();
    }

    private LocalDateTime dueAt(Payment payment, SimulatorConfig.MethodSimulatorConfig methodConfig) {
        int range = methodConfig.getMaxDelaySeconds() - methodConfig.getMinDelaySeconds();
        int delaySeconds = methodConfig.getMinDelaySeconds() + Math.abs(payment.getId().hashCode()) % (range + 1);

        if (simulatorConfig.getChaosMode() == ChaosMode.SLOW) {
            delaySeconds *= 2; // Double the delay for slow chaos mode
        }

        return payment.getCreatedAt().plusSeconds(delaySeconds);

    }


}
