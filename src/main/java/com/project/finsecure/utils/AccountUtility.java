package com.project.finsecure.utils;

import com.project.finsecure.dto.BankResponse;

import java.util.Random;

public class AccountUtility {
    public static String ACCOUNT_FOUND = "001";
    public static String ACCOUNT_EXISTS_CODE = "002";
    public static String ACCOUNT_CREATION_SUCCESS = "002";
    public static String ACCOUNT_NOT_EXISTS = "004";
    public static String ACCOUNT_CREDITED_SUCCESS = "005";
    public static String ACCOUNT_EXISTS_MESSAGE = "This user already has an account Created!";
    public static String ACCOUNT_CREATION_SUCCESS_MESSAGE = "The account has been created successfully!";
    public static String ACCOUNT_NOT_EXISTS_MESSAGE = "User with provided Account Number does not exist";
    public static String ACCOUNT_FOUND_MESSAGE = "User with provided Account Number found successfully";
    public static String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "User account credited successfully";

    public static String generateAccountNumber() {
        // Get current time in milliseconds (Unix timestamp)
        long timestamp = System.currentTimeMillis() % 10000000000L; // last 10 digits of timestamp

        // Generate a random 2-digit number
        Random random = new Random();
        int randomTwoDigits = 10 + random.nextInt(90); // Ensures 2 digits

        // Combine timestamp and random number
        return timestamp + String.valueOf(randomTwoDigits);
    }

    public static BankResponse accountNotExistResponse() {
        return BankResponse.builder()
                .responseCode(AccountUtility.ACCOUNT_NOT_EXISTS)
                .responseMessage(AccountUtility.ACCOUNT_NOT_EXISTS_MESSAGE)
                .accountInfo(null)
                .build();
    }

}
