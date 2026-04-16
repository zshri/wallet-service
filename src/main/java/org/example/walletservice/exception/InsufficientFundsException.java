package org.example.walletservice.exception;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class InsufficientFundsException extends RuntimeException {

    private final UUID walletId;
    private final BigDecimal requestedAmount;
    private final BigDecimal availableBalance;

    public InsufficientFundsException(UUID walletId, BigDecimal requestedAmount, BigDecimal availableBalance) {
        super("Insufficient funds in wallet");
        this.walletId = walletId;
        this.requestedAmount = requestedAmount;
        this.availableBalance = availableBalance;
    }

}