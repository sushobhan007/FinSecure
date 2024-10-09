package com.project.finsecure.service;

import com.project.finsecure.dto.*;
import com.project.finsecure.entity.User;
import com.project.finsecure.repository.UserRepository;
import com.project.finsecure.utils.AccountUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            System.out.println("Account already exists for the email: " + userRequest.getEmail());
            return buildErrorResponse(AccountUtility.ACCOUNT_EXISTS_CODE,
                    AccountUtility.ACCOUNT_EXISTS_MESSAGE);
        }

        User newUser = buildNewUser(userRequest);
        User savedUser = userRepository.save(newUser);

        sendAccountCreationEmail(savedUser);

        System.out.println("Account created successfully for the user: " + savedUser.getEmail());
        return buildSuccessResponse(AccountUtility.ACCOUNT_CREATION_SUCCESS,
                AccountUtility.ACCOUNT_CREATION_SUCCESS_MESSAGE,
                savedUser);
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        User user = getUserByAccountNumber(enquiryRequest.getAccountNumber());
        if (user == null) {
            return AccountUtility.accountNotExistResponse();
        }

        return buildSuccessResponse(AccountUtility.ACCOUNT_FOUND,
                AccountUtility.ACCOUNT_FOUND_MESSAGE,
                user);
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        User user = getUserByAccountNumber(enquiryRequest.getAccountNumber());
        return (user == null) ? AccountUtility.ACCOUNT_NOT_EXISTS_MESSAGE : getUserFullName(user);
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditDebitRequest) {
        User userToCredit = getUserByAccountNumber(creditDebitRequest.getAccountNumber());
        if (userToCredit == null) {
            return AccountUtility.accountNotExistResponse();
        }

        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitRequest.getAmount()));
        userRepository.save(userToCredit);

        return buildSuccessResponse(AccountUtility.ACCOUNT_CREDITED_SUCCESS,
                AccountUtility.ACCOUNT_CREDITED_SUCCESS_MESSAGE,
                userToCredit);
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest creditDebitRequest) {
        User userToDebit = getUserByAccountNumber(creditDebitRequest.getAccountNumber());
        if (userToDebit == null) {
            return AccountUtility.accountNotExistResponse();
        }

        if (userToDebit.getAccountBalance().compareTo(creditDebitRequest.getAmount()) < 0) {
            return buildErrorResponse(AccountUtility.INSUFFICIENT_ACCOUNT_BALANCE,
                    AccountUtility.INSUFFICIENT_ACCOUNT_BALANCE_MESSAGE);
        }

        userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(creditDebitRequest.getAmount()));
        userRepository.save(userToDebit);

        return buildSuccessResponse(AccountUtility.ACCOUNT_DEBITED_SUCCESS,
                AccountUtility.ACCOUNT_DEBITED_SUCCESS_MESSAGE,
                userToDebit);
    }

    private User getUserByAccountNumber(String accountNumber) {
        return userRepository.findByAccountNumber(accountNumber);
    }

    private String getUserFullName(User user) {
        return String.join(" ", user.getFirstName().trim(), user.getMiddleName().trim(), user.getLastName().trim());
    }

    private User buildNewUser(UserRequest userRequest) {
        return User.builder()
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
    }

    private void sendAccountCreationEmail(User savedUser) {
        String accountName = getUserFullName(savedUser);
        String emailBody = String.format("""
                Hello user,
                                
                Your account has been created successfully at our bank.
                Here are your details. Please save this email and keep the information safe.
                                
                Name: %s
                Account Number: %s
                Account Activation Date: %s
                                
                Thank you for using our service.
                Regards,
                FinSecure
                """, accountName, savedUser.getAccountNumber(), savedUser.getCreatedOn());

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("Account Created Successfully!")
                .messageBody(emailBody)
                .build();

        emailService.sendEmailAlert(emailDetails);
    }

    private BankResponse buildErrorResponse(String responseCode, String responseMessage) {
        return BankResponse.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .accountInfo(null)
                .build();
    }

    private BankResponse buildSuccessResponse(String responseCode, String responseMessage, User user) {
        return BankResponse.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .accountInfo(
                        AccountInfo.builder()
                                .accountName(getUserFullName(user))
                                .accountNumber(user.getAccountNumber())
                                .accountBalance(user.getAccountBalance())
                                .build()
                )
                .build();
    }
}
