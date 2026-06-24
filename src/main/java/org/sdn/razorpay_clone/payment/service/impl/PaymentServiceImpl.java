package org.sdn.razorpay_clone.payment.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.sdn.razorpay_clone.common.enums.OrderStatus;
import org.sdn.razorpay_clone.common.enums.PaymentStatus;
import org.sdn.razorpay_clone.common.exception.BusinessRuleViolationException;
import org.sdn.razorpay_clone.common.exception.ResourceNotFoundException;
import org.sdn.razorpay_clone.payment.dto.request.PaymentInitRequest;
import org.sdn.razorpay_clone.payment.dto.response.PaymentResponse;
import org.sdn.razorpay_clone.payment.entity.OrderRecord;
import org.sdn.razorpay_clone.payment.entity.Payment;
import org.sdn.razorpay_clone.payment.gateway.PaymentStrategy;
import org.sdn.razorpay_clone.payment.gateway.PaymentStrategyFactory;
import org.sdn.razorpay_clone.payment.gateway.dto.PaymentRequest;
import org.sdn.razorpay_clone.payment.gateway.dto.PaymentResult;
import org.sdn.razorpay_clone.payment.mapper.PaymentMapper;
import org.sdn.razorpay_clone.payment.repository.OrderRepository;
import org.sdn.razorpay_clone.payment.repository.PaymentRepository;
import org.sdn.razorpay_clone.payment.service.PaymentService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImpl implements PaymentService {
    PaymentRepository paymentRepository;
    OrderRepository orderRepository;
    PaymentStrategyFactory paymentStrategyFactory;
    PaymentMapper paymentMapper;

    @Transactional
    @Override
    public PaymentResponse initiate(UUID merchantId, PaymentInitRequest request) {
        OrderRecord order = this.orderRepository.findByIdAndMerchantId(request.orderId(), merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found", request.orderId()));

        if (order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.ATTEMPTED) {
            throw new BusinessRuleViolationException("ORDER_NOT_PAYABLE", "Order cannot accept payment in status" + order.getStatus());
        }

        order.setStatus(OrderStatus.ATTEMPTED);
        order.setAttempts(order.getAttempts() + 1);

        Payment payment = Payment.builder()
                .order(order)
                .merchantId(merchantId)
                .amount(order.getAmount())
                .paymentStatus(PaymentStatus.CREATED)
                .method(request.method())
                .methodDetails(request.methodDetails())
                .build();

        payment = paymentRepository.save(payment);

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .paymentId(payment.getId())
                .orderId(request.orderId())
                .merchantId(merchantId)
                .amount(payment.getAmount())
                .method(request.method())
                .methodDetails(request.methodDetails())
                .build();

        PaymentResult result = this.paymentStrategyFactory.getPaymentStrategy(request.method()).initiate(paymentRequest);

        switch (result) {
            case PaymentResult.Failure failure -> {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
            case PaymentResult.Pending pending -> {
                payment.setProcessorReference(pending.registrationRef());
            }
        }

        payment = paymentRepository.save(payment);
        orderRepository.save(order);
        return paymentMapper.toPaymentResponse(payment);
    }
}
