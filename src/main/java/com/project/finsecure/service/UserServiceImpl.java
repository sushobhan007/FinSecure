package com.project.finsecure.service;


import com.project.finsecure.dto.*;
import com.project.finsecure.entity.User;
import com.project.finsecure.repository.UserRepository;
import com.project.finsecure.utils.AccountUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

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
                .email(userRequest.getEmail().toLowerCase())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);

        String accountName = getUserFullName(savedUser);

        //Send Email Alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("Account Created Successfully!")
                .messageBody(String.format("""
                        Hello user,
                                                
                        Your account is created successfully to our bank.
                        Here is your details, please save this email.
                        Note down the details and keep the details safe.
                                                
                        Name: %s
                        Account Number: %s
                        Account Activation Date: %s
                                                
                        Thank you for using our service.
                        Regards,
                        FinSecure
                        """, accountName, savedUser.getAccountNumber(), savedUser.getCreatedOn()))
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtility.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtility.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                .accountInfo(
                        AccountInfo.builder()
                                .accountNumber(savedUser.getAccountNumber())
                                .accountBalance(savedUser.getAccountBalance())
                                .accountName(accountName)
                                .build()
                )
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        Boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtility.accountNotExistResponse();
        }

        User user = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtility.ACCOUNT_FOUND)
                .responseMessage(AccountUtility.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(
                        AccountInfo.builder()
                                .accountName(getUserFullName(user))
                                .accountNumber(user.getAccountNumber())
                                .accountBalance(user.getAccountBalance())
                                .build()
                )
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        Boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtility.ACCOUNT_NOT_EXISTS_MESSAGE;
        }
        User user = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return getUserFullName(user);
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditDebitRequest) {
        Boolean isAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtility.accountNotExistResponse();
        }
        User userToCredit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitRequest.getAmount()));
        userRepository.save(userToCredit);

        return BankResponse.builder()
                .responseCode(AccountUtility.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtility.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(
                        AccountInfo.builder()
                                .accountName(getUserFullName(userToCredit))
                                .accountNumber(userToCredit.getAccountNumber())
                                .accountBalance(userToCredit.getAccountBalance())
                                .build()
                )
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest creditDebitRequest) {
        Boolean isAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtility.accountNotExistResponse();
        }
        User userToDebit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
        BigInteger accountBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = creditDebitRequest.getAmount().toBigInteger();

        if (accountBalance.intValue() == 0 || debitAmount.intValue() > accountBalance.intValue()) {
            return BankResponse.builder()
                    .responseCode(AccountUtility.INSUFFICIENT_ACCOUNT_BALANCE)
                    .responseMessage(AccountUtility.INSUFFICIENT_ACCOUNT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(creditDebitRequest.getAmount()));
        userRepository.save(userToDebit);

        return BankResponse.builder()
                .responseCode(AccountUtility.ACCOUNT_DEBITED_SUCCESS)
                .responseMessage(AccountUtility.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                .accountInfo(
                        AccountInfo.builder()
                                .accountName(getUserFullName(userToDebit))
                                .accountNumber(userToDebit.getAccountNumber())
                                .accountBalance(userToDebit.getAccountBalance())
                                .build()
                )
                .build();
    }

    private String getUserFullName(User user) {
        return user.getFirstName().trim() + " " +
                user.getMiddleName().trim() + " " +
                user.getLastName().trim();
    }
}
