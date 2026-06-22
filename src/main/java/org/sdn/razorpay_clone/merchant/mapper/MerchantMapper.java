package org.sdn.razorpay_clone.merchant.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.sdn.razorpay_clone.common.enums.MerchantStatus;
import org.sdn.razorpay_clone.merchant.dto.request.MerchantSignUpRequest;
import org.sdn.razorpay_clone.merchant.dto.response.MerchantResponse;
import org.sdn.razorpay_clone.merchant.entity.Merchant;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
public interface MerchantMapper {

    MerchantResponse toMerchantResponse(Merchant merchant);


    @Mapping(target = "status", source = "status")
    Merchant toMerchant(MerchantSignUpRequest merchantSignUpRequest, MerchantStatus status);
}
