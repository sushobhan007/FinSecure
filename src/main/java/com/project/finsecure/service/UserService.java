package com.project.finsecure.service;


import com.project.finsecure.dto.BankResponse;
import com.project.finsecure.dto.EnquiryRequest;
import com.project.finsecure.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);

    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);

    String nameEnquiry(EnquiryRequest enquiryRequest);
}
