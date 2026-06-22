package org.sdn.razorpay_clone.payment.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.payment.dto.request.CreateOrderRequest;
import org.sdn.razorpay_clone.payment.dto.response.OrderResponse;
import org.sdn.razorpay_clone.payment.dto.response.PaymentResponse;
import org.sdn.razorpay_clone.payment.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v1/orders")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;
    UUID merchantId = UUID.fromString("ca788752-1dbd-4453-9d33-709e9d8e85db"); // TODO: Take from security context

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(merchantId, request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> get(@PathVariable UUID orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getById(merchantId, orderId));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> delete(@PathVariable UUID orderId) {
        orderService.cancel(merchantId, orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orderId}/payments")
    public ResponseEntity<List<PaymentResponse>> getAllOrders(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.listPayments(merchantId, orderId));
    }

}
