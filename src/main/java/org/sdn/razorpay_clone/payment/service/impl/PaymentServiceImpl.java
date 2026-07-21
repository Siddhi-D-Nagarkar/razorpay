package org.sdn.razorpay_clone.payment.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.sdn.razorpay_clone.common.enums.OrderStatus;
import org.sdn.razorpay_clone.common.enums.PaymentEvent;
import org.sdn.razorpay_clone.common.enums.PaymentStatus;
import org.sdn.razorpay_clone.common.exception.BusinessRuleViolationException;
import org.sdn.razorpay_clone.common.exception.ResourceNotFoundException;
import org.sdn.razorpay_clone.payment.dto.request.PaymentInitRequest;
import org.sdn.razorpay_clone.payment.dto.response.PaymentResponse;
import org.sdn.razorpay_clone.payment.entity.OrderRecord;
import org.sdn.razorpay_clone.payment.entity.Payment;
import org.sdn.razorpay_clone.payment.gateway.PaymentGatewayRouter;
import org.sdn.razorpay_clone.payment.gateway.PaymentStrategy;
import org.sdn.razorpay_clone.payment.gateway.PaymentStrategyFactory;
import org.sdn.razorpay_clone.payment.gateway.dto.PaymentRequest;
import org.sdn.razorpay_clone.payment.gateway.dto.PaymentResult;
import org.sdn.razorpay_clone.payment.mapper.PaymentMapper;
import org.sdn.razorpay_clone.payment.repository.OrderRepository;
import org.sdn.razorpay_clone.payment.repository.PaymentRepository;
import org.sdn.razorpay_clone.payment.service.PaymentService;
import org.sdn.razorpay_clone.payment.statemachine.PaymentTransitionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    PaymentGatewayRouter paymentGatewayRouter;
    PaymentTransitionService paymentTransitionService;

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
                .idempotencyKey(UUID.randomUUID().toString())
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

        paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_ATTEMPT);
        PaymentResult result = this.paymentStrategyFactory.getPaymentStrategy(request.method()).initiate(paymentRequest);

        switch (result) {
            case PaymentResult.Failure failure -> {
//                payment.setPaymentStatus(PaymentStatus.FAILED);
                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
            case PaymentResult.Pending pending -> {
                payment.setProcessorReference(pending.registrationRef());
            }
            case PaymentResult.Success success -> {
                log.warn("Invalid Case");
                return null;
            }
        }

        payment = paymentRepository.save(payment);
        orderRepository.save(order);
        return paymentMapper.toPaymentResponse(payment);
    }

    @Transactional
    @Override
    public PaymentResponse capture(UUID merchantId, UUID paymentId) {
        Payment payment = this.paymentRepository.findByIdAndMerchantId(paymentId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment Not Found", paymentId.toString()));

//        payment.setPaymentStatus(PaymentStatus.CAPTURING);
        paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
        PaymentResult paymentResult = paymentGatewayRouter.capture(payment.getMethod(), paymentId);

        if (paymentResult instanceof PaymentResult.Success) {
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
//            payment.setPaymentStatus(PaymentStatus.CAPTURED);
            payment.setCapturedAt(LocalDateTime.now());
            log.info("Payment Captured with PaymentId: {}", paymentId);
        } else if (paymentResult instanceof PaymentResult.Failure failure) {
//            payment.setPaymentStatus(PaymentStatus.AUTHORIZED);
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
            payment.setErrorCode(failure.errorCode());
            payment.setErrorDescription(failure.errorDescription());
            log.warn("Payment Capture Failed with PaymentId: {} ErrorCode: {} ErrorDescription: {}", paymentId, failure.errorCode(), failure.errorDescription());
        }


        return paymentMapper.toPaymentResponse(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public void resolveAuthorization(UUID paymentId, boolean approve, String bankRef, String errorCode, String errorDescription) {
        Payment payment = this.paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment Not Found", paymentId.toString()));
        // Implementation for resolving authorization

        if (payment.getPaymentStatus() != PaymentStatus.AUTHORIZING) {
            log.warn("Payment {} is not in AUTHORIZING state. Current state: {}", paymentId, payment.getPaymentStatus());
            return;
        }

        OrderRecord orderRecord = payment.getOrder();


        if (approve) {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankRef);
            payment.setAuthorizedAt(LocalDateTime.now());

//            AUTO CAPTURE
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
            PaymentResult captureResult = paymentGatewayRouter.capture(payment.getMethod(), paymentId);

            if (captureResult instanceof PaymentResult.Success success) {
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
                payment.setCapturedAt(LocalDateTime.now());
                orderRecord.setStatus(OrderStatus.PAID);

            } else if (captureResult instanceof PaymentResult.Failure failure) {
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
            log.info("Payment {} authorized successfully with bank reference {}", paymentId, bankRef);
        } else {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
            log.info("Payment {} authorization failed with error code {} and description {}", paymentId, errorCode, errorDescription);

        }

        paymentRepository.save(payment);
        orderRepository.save(orderRecord);

    }
}
