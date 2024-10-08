package com.project.finsecure.utils;

import java.util.Random;

public class AccountUtility {
    public static String ACCOUNT_FOUND = "200";
    public static String ACCOUNT_EXISTS_CODE = "409";
    public static String ACCOUNT_CREATION_SUCCESS = "201";
    public static String ACCOUNT_NOT_EXISTS = "400";
    public static String ACCOUNT_EXISTS_MESSAGE = "This user already has an account Created!";
    public static String ACCOUNT_CREATION_SUCCESS_MESSAGE = "The account has been created successfully!";
    public static String ACCOUNT_NOT_EXISTS_MESSAGE = "User with provided Account Number does not exist";
    public static String ACCOUNT_FOUND_MESSAGE = "User with provided Account Number found successfully";

    public static String generateAccountNumber() {
        // Get current time in milliseconds (Unix timestamp)
        long timestamp = System.currentTimeMillis() % 10000000000L; // last 10 digits of timestamp

        // Generate a random 2-digit number
        Random random = new Random();
        int randomTwoDigits = 10 + random.nextInt(90); // Ensures 2 digits

        // Combine timestamp and random number
        return timestamp + String.valueOf(randomTwoDigits);
    }

}
