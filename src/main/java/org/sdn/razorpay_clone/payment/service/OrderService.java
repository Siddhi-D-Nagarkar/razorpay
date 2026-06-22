package org.sdn.razorpay_clone.payment.service;

import jakarta.validation.Valid;
import org.sdn.razorpay_clone.payment.dto.request.CreateOrderRequest;
import org.sdn.razorpay_clone.payment.dto.response.OrderResponse;
import org.sdn.razorpay_clone.payment.dto.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse create(UUID merchantId, @Valid CreateOrderRequest request);

    OrderResponse getById(UUID merchantId, UUID orderId);

    OrderResponse cancel(UUID merchantId, UUID orderId);

    List<PaymentResponse> listPayments(UUID merchantId,UUID orderId);
}
