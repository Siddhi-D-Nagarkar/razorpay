package org.sdn.razorpay_clone.payment.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.sdn.razorpay_clone.common.enums.OrderStatus;
import org.sdn.razorpay_clone.common.exception.BusinessRuleViolationException;
import org.sdn.razorpay_clone.common.exception.DuplicateResourceException;
import org.sdn.razorpay_clone.common.exception.ResourceNotFoundException;
import org.sdn.razorpay_clone.payment.dto.request.CreateOrderRequest;
import org.sdn.razorpay_clone.payment.dto.response.OrderResponse;
import org.sdn.razorpay_clone.payment.dto.response.PaymentResponse;
import org.sdn.razorpay_clone.payment.entity.OrderRecord;
import org.sdn.razorpay_clone.payment.entity.Payment;
import org.sdn.razorpay_clone.payment.mapper.OrderMapper;
import org.sdn.razorpay_clone.payment.mapper.PaymentMapper;
import org.sdn.razorpay_clone.payment.repository.OrderRepository;
import org.sdn.razorpay_clone.payment.repository.PaymentRepository;
import org.sdn.razorpay_clone.payment.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {
    OrderRepository orderRepository;
    PaymentRepository paymentRepository;
    PaymentMapper paymentMapper;
    OrderMapper orderMapper;

    @Value("${razorpay.order.default-expiry-minutes:30}")
    @NonFinal
    int defaultOrderExpiryMinutes;

    @Override
    public OrderResponse create(UUID merchantId, CreateOrderRequest request) {
        if (request.receipt() != null &&
                orderRepository.existsByMerchantIdAndReceipt(merchantId, request.receipt())) {
            log.error("Order with receipt {} already exists", request.receipt());
            throw new DuplicateResourceException("ORDER_RECEIPT_DUPLICATE", "Order with receipt " + request.receipt() + " already exists");
        }

        OrderRecord newOrderRecord = OrderRecord.builder()
                .amount(request.amount())
                .receipt(request.receipt())
                .notes(request.notes())
                .merchantId(merchantId)
                .status(OrderStatus.CREATED)
                .expiresAt(request.expiresAt() != null ? request.expiresAt() :
                        LocalDateTime.now().plusMinutes(defaultOrderExpiryMinutes))
                .build();

        newOrderRecord = this.orderRepository.save(newOrderRecord);

        // TODO: send kafka event for order created
        return orderMapper.toOrderResponse(newOrderRecord);
    }

    @Override
    public OrderResponse getById(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord = orderRepository.findByMerchantIdAndId(merchantId, orderId)
                .orElseThrow(() -> {
                    log.error("Order with id {} not found for merchant {}", orderId, merchantId);
                    return new ResourceNotFoundException("ORDER", orderId);
                });

        return orderMapper.toOrderResponse(orderRecord);
    }

    @Transactional
    @Override
    public OrderResponse cancel(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord = orderRepository.findByMerchantIdAndId(merchantId, orderId)
                .orElseThrow(() -> {
                    log.error("Order with id {} not found for merchant {}", orderId, merchantId);
                    return new ResourceNotFoundException("ORDER", orderId);
                });

        if (orderRecord.getStatus().equals(OrderStatus.PAID) || orderRecord.getStatus().equals(OrderStatus.CANCELED)) {
            log.error("Cannot delete order with id {} as it is already paid", orderId);
            throw new BusinessRuleViolationException("ORDER_CANNOT_CANCEL",
                    "Cannot cancel order with status " + orderRecord.getStatus().name());
        }
        orderRecord.setStatus(OrderStatus.CANCELED);

        return orderMapper.toOrderResponse(orderRecord);
    }

    @Override
    public List<PaymentResponse> listPayments(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord = orderRepository.findByMerchantIdAndId(merchantId, orderId)
                .orElseThrow(() -> {
                    log.error("Order with id {} not found for merchant {}", orderId, merchantId);
                    return new ResourceNotFoundException("ORDER", orderId);
                });

        List<Payment> paymentList = paymentRepository.findByOrderId(orderRecord.getId());


        return paymentList.stream()
                .map(paymentMapper::toPaymentResponse)
                .toList();
    }
}
