package com.project.finsecure.service;

import com.project.finsecure.dto.TransactionDetails;
import com.project.finsecure.entity.Transaction;
import com.project.finsecure.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDetails transactionDetails) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDetails.getTransactionType())
                .accountNumber(transactionDetails.getAccountNumber())
                .amount(transactionDetails.getAmount())
                .status("SUCCESS")
                .build();
        transactionRepository.save(transaction);
        System.out.printf("Transaction for accountNumber %s is saved successfully%n",
                transactionDetails.getAccountNumber());
    }
}
