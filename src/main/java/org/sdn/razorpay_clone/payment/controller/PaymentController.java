package org.sdn.razorpay_clone.payment.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.payment.dto.request.PaymentInitRequest;
import org.sdn.razorpay_clone.payment.dto.response.PaymentResponse;
import org.sdn.razorpay_clone.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/v1/payments")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    PaymentService paymentService;
    UUID merchantId = UUID.fromString("ca788752-1dbd-4453-9d33-709e9d8e85db"); // TODO: Take from security context

    @PostMapping
    public ResponseEntity<PaymentResponse> initiate(@RequestBody @Valid PaymentInitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.initiate(merchantId, request));
    }
}
