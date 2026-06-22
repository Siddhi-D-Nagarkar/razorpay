package org.sdn.razorpay_clone.payment.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.sdn.razorpay_clone.payment.dto.response.PaymentResponse;
import org.sdn.razorpay_clone.payment.entity.Payment;

import java.util.List;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
public interface PaymentMapper {


//    @Mapping(target = "status", source = "")
    @Mapping(target = "orderId", source = "order.id")
//    @Mapping(target = "createdAt", source = "")
    PaymentResponse toPaymentResponse(Payment payment);

    List<PaymentResponse> toPaymentResponseList(List<Payment> payments);
}
