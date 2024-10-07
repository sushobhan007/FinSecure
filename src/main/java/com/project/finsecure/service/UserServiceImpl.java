package com.project.finsecure.service;


import com.project.finsecure.dto.AccountInfo;
import com.project.finsecure.dto.BankResponse;
import com.project.finsecure.dto.EmailDetails;
import com.project.finsecure.dto.UserRequest;
import com.project.finsecure.entity.User;
import com.project.finsecure.repository.UserRepository;
import com.project.finsecure.utils.AccountUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtility.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtility.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .middleName(userRequest.getMiddleName())
                .lastName(userRequest.getLastName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtility.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);

        String accountName = savedUser.getFirstName().trim() + " " +
                savedUser.getMiddleName().trim() + " " +
                savedUser.getLastName().trim();

        //Send Email Alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("Account Created Successfully!")
                .messageBody(String.format("""
                        Hello user,
                        Your account is created successfully to our bank.
                        Here is your details, please save this email.
                        Note down the details and keep the details safe.
                        Name: %s %n
                        Account Number: %s
                        Account Activation Date: %s
                        """, accountName, savedUser.getAccountNumber(), savedUser.getCreatedOn()))
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtility.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtility.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(
                        AccountInfo.builder()
                                .accountNumber(savedUser.getAccountNumber())
                                .accountBalance(savedUser.getAccountBalance())
                                .accountName(accountName)
                                .build()
                )
                .build();
    }
}
