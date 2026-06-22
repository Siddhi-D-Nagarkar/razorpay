package org.sdn.razorpay_clone.merchant.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.sdn.razorpay_clone.merchant.dto.response.ApiKeyCreateResponse;
import org.sdn.razorpay_clone.merchant.dto.response.ApiKeyResponse;
import org.sdn.razorpay_clone.merchant.entity.ApiKey;

import java.util.List;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
public interface ApiKeyMapper {

    @Mapping(target = "keySecret", source = "keySecret")
    ApiKeyCreateResponse toApiKeyCreateResponse(ApiKey apiKey,String keySecret);

    ApiKeyResponse toApiKeyResponse(ApiKey apiKey);

    List<ApiKeyResponse> toApiKeyResponseList(List<ApiKey> apiKeys);
}
