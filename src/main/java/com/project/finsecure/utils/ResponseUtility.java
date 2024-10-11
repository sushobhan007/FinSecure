package com.project.finsecure.utils;

import com.project.finsecure.dto.AccountInfo;
import com.project.finsecure.dto.BankResponse;
import com.project.finsecure.entity.User;

public class ResponseUtility {
    public static BankResponse buildSuccessResponse(String responseCode, String responseMessage, User user) {
        AccountInfo accountInfo = user == null ? null : AccountInfo.builder()
                .accountName(UserUtility.getUserFullName(user))
                .accountNumber(user.getAccountNumber())
                .accountBalance(user.getAccountBalance())
                .build();
        return BankResponse.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .accountInfo(accountInfo)
                .build();
    }

    public static BankResponse buildErrorResponse(String responseCode, String responseMessage) {
        return BankResponse.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .accountInfo(null)
                .build();
    }
}
