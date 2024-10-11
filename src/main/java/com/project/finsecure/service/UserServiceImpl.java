package com.project.finsecure.service;

import com.project.finsecure.dto.*;
import com.project.finsecure.entity.User;
import com.project.finsecure.repository.UserRepository;
import com.project.finsecure.utils.AccountUtility;
import com.project.finsecure.utils.EmailUtility;
import com.project.finsecure.utils.ResponseUtility;
import com.project.finsecure.utils.UserUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            System.out.println("Account already exists for the email: " + userRequest.getEmail());
            return ResponseUtility.buildErrorResponse(AccountUtility.ACCOUNT_EXISTS_CODE,
                    AccountUtility.ACCOUNT_EXISTS_MESSAGE);
        }

        User newUser = buildNewUser(userRequest);
        User savedUser = userRepository.save(newUser);

        EmailUtility.sendAccountCreationEmail(savedUser, emailService);

        System.out.println("Account created successfully for the user: " + savedUser.getEmail());
        return ResponseUtility.buildSuccessResponse(AccountUtility.ACCOUNT_CREATION_SUCCESS,
                AccountUtility.ACCOUNT_CREATION_SUCCESS_MESSAGE,
                savedUser);
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        User user = getUserByAccountNumber(enquiryRequest.getAccountNumber());
        if (user == null) {
            return AccountUtility.accountNotExistResponse();
        }

        return ResponseUtility.buildSuccessResponse(AccountUtility.ACCOUNT_FOUND,
                AccountUtility.ACCOUNT_FOUND_MESSAGE,
                user);
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        User user = getUserByAccountNumber(enquiryRequest.getAccountNumber());
        return (user == null) ? AccountUtility.ACCOUNT_NOT_EXISTS_MESSAGE : UserUtility.getUserFullName(user);
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditDebitRequest) {
        User userToCredit = getUserByAccountNumber(creditDebitRequest.getAccountNumber());
        if (userToCredit == null) {
            return AccountUtility.accountNotExistResponse();
        }

        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitRequest.getAmount()));
        userRepository.save(userToCredit);

        //save transaction
        saveTransaction(userToCredit, creditDebitRequest, "CREDIT");

        EmailUtility.sendAccountCreditEmail(userToCredit, creditDebitRequest.getAmount(), emailService);

        return ResponseUtility.buildSuccessResponse(AccountUtility.ACCOUNT_CREDITED_SUCCESS,
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
            return ResponseUtility.buildErrorResponse(AccountUtility.INSUFFICIENT_ACCOUNT_BALANCE,
                    AccountUtility.INSUFFICIENT_ACCOUNT_BALANCE_MESSAGE);
        }

        userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(creditDebitRequest.getAmount()));
        userRepository.save(userToDebit);

        //save transaction
        saveTransaction(userToDebit, creditDebitRequest, "DEBIT");

        EmailUtility.sendAccountDebitEmail(userToDebit, creditDebitRequest.getAmount(), emailService);

        return ResponseUtility.buildSuccessResponse(AccountUtility.ACCOUNT_DEBITED_SUCCESS,
                AccountUtility.ACCOUNT_DEBITED_SUCCESS_MESSAGE,
                userToDebit);
    }

    @Override
    public BankResponse transfer(TransferRequest transferRequest) {

        //get the account to credit

        Boolean sourceAccountExists = userRepository.existsByAccountNumber(transferRequest.getSourceAccountNumber());

        //get the account to debit (check if account exists)
        Boolean destinationAccountExists = userRepository.existsByAccountNumber(transferRequest.getDestinationAccountNumber());
        if (!destinationAccountExists || !sourceAccountExists) {
            return AccountUtility.accountNotExistResponse();
        }
        User sourceAccountUser = userRepository.findByAccountNumber(transferRequest.getSourceAccountNumber());

        //check if debit amount is not more than the current amount
        if (transferRequest.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
            return ResponseUtility.buildErrorResponse(AccountUtility.INSUFFICIENT_ACCOUNT_BALANCE,
                    AccountUtility.INSUFFICIENT_ACCOUNT_BALANCE_MESSAGE);
        }

        //debit the amount
        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(transferRequest.getAmount()));
        userRepository.save(sourceAccountUser);

//        //save debit transaction
//        saveTransaction(userToDebit, creditDebitRequest, "DEBIT");

        //credit the account
        User destinationAccountUser = userRepository.findByAccountNumber(transferRequest.getDestinationAccountNumber());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(transferRequest.getAmount()));
        userRepository.save(destinationAccountUser);

//        //save credit transaction
//        saveTransaction(userToCredit, creditDebitRequest, "CREDIT");

        EmailUtility.sendAccountDebitEmail(sourceAccountUser, transferRequest.getAmount(), emailService);
        EmailUtility.sendAccountCreditEmail(destinationAccountUser, transferRequest.getAmount(), emailService);

        return ResponseUtility.buildSuccessResponse(AccountUtility.TRANSFER_SUCCESS,
                AccountUtility.TRANSFER_SUCCESS_MESSAGE,
                null);
    }

    private User getUserByAccountNumber(String accountNumber) {
        return userRepository.findByAccountNumber(accountNumber);
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

    void saveTransaction(User user, CreditDebitRequest request, String transactionType) {
        //save transaction
        TransactionDetails creditTransaction = TransactionDetails.builder()
                .accountNumber(user.getAccountNumber())
                .transactionType(transactionType)
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(creditTransaction);
    }
}
