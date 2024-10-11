package com.project.finsecure.service;

import com.project.finsecure.dto.TransactionDetails;


public interface TransactionService {
    void saveTransaction(TransactionDetails transactionDetails);
}
