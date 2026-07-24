package org.sdn.razorpay_clone.payment.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.merchant.security.MerchantContext;
import org.sdn.razorpay_clone.payment.dto.request.PaymentInitRequest;
import org.sdn.razorpay_clone.payment.dto.response.PaymentResponse;
import org.sdn.razorpay_clone.payment.dto.response.TransactionResponse;
import org.sdn.razorpay_clone.payment.service.PaymentService;
import org.sdn.razorpay_clone.payment.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/payments")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    PaymentService paymentService;
    TransactionService transactionService;
    MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<PaymentResponse> initiate(@RequestBody @Valid PaymentInitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.initiate(merchantContext.getMerchantId(), request));
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<PaymentResponse> capture(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.capture(merchantContext.getMerchantId(), paymentId));
    }

    /**
     * Test endpoint to simulate circuit breaker failure scenarios.
     * This endpoint demonstrates the @CircuitBreaker annotation behavior.
     *
     * Query Parameters:
     * - type: Transaction type (default: "SUCCESS")
     *   - "SUCCESS": Normal successful transaction
     *   - "FAILURE": Simulates a failure to trigger circuit breaker
     *   - "SLOW": Simulates a slow service (timeout scenario)
     *
     * Examples:
     * - GET /v1/payments/test/circuit-breaker → Success case
     * - GET /v1/payments/test/circuit-breaker?type=FAILURE → Trigger circuit breaker
     * - GET /v1/payments/test/circuit-breaker?type=SLOW → Timeout scenario
     *
     * @param transactionType the type of transaction to simulate
     * @return TransactionResponse with circuit breaker status
     */
    @GetMapping("/test/circuit-breaker")
    public ResponseEntity<TransactionResponse> testCircuitBreaker(
            @RequestParam(name = "type", defaultValue = "SUCCESS") String transactionType) {
        return ResponseEntity.ok(transactionService.simulateTransaction(merchantContext.getMerchantId(), transactionType));
    }
}
