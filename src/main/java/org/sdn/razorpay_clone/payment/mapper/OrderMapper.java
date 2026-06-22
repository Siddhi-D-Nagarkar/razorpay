package org.sdn.razorpay_clone.payment.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.sdn.razorpay_clone.payment.dto.response.OrderResponse;
import org.sdn.razorpay_clone.payment.entity.OrderRecord;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
public interface OrderMapper {
    OrderResponse toOrderResponse(OrderRecord orderRecord);
}
