package com.project.finsecure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDetails {
    private String transactionType;
    private BigDecimal amount;
    private String accountNumber;
    private String status;
}
