package org.sdn.razorpay_clone.merchant.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.sdn.razorpay_clone.common.exception.ResourceNotFoundException;
import org.sdn.razorpay_clone.merchant.entity.Customer;
import org.sdn.razorpay_clone.merchant.entity.Merchant;
import org.sdn.razorpay_clone.merchant.repository.CustomerRepository;
import org.sdn.razorpay_clone.merchant.repository.MerchantRepository;
import org.sdn.razorpay_clone.merchant.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    CustomerRepository customerRepository;
    MerchantRepository merchantRepository;

    @Override
    @Transactional
    public UUID findOrCreate(UUID merchantId, String email, String name, String phone) {
        if (email == null || email.isBlank()) {
            return null;
        }


        return customerRepository.findByMerchant_IdAndEmail(merchantId, email).
                map(customer -> customer.getId())
                .orElseGet(() -> createNew(merchantId, email, name, phone));
    }


    private UUID createNew(UUID merchantId, String email, String name, String phone) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", merchantId));

        Customer customer = Customer.builder()
                .merchant(merchant)
                .email(email)
                .name(name)
                .phone(phone)
                .build();

        customer = customerRepository.save(customer);
        log.info("Customer created via findOrCreate id={} merchantId={} email={}",
                customer.getId(), merchantId, email);
        return customer.getId();
    }
}
