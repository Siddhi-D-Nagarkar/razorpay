package org.sdn.razorpay_clone.payment.controller;


import lombok.RequiredArgsConstructor;
import org.sdn.razorpay_clone.payment.dto.request.CreateOrderRequest;
import org.sdn.razorpay_clone.payment.dto.response.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/orders")
@RestController
@RequiredArgsConstructor
public class OrderController {

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request){
        return ResponseEntity.ok(new OrderResponse());
    }
}
