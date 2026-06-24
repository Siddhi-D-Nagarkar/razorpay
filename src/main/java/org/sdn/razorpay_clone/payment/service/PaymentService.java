package org.sdn.razorpay_clone.payment.service;

import org.sdn.razorpay_clone.payment.dto.request.PaymentInitRequest;
import org.sdn.razorpay_clone.payment.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse initiate(UUID merchantId, PaymentInitRequest request);
}
