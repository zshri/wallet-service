package org.example.walletservice.service;

import org.example.walletservice.dto.WalletOperationRequest;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletService {

    void processOperation(WalletOperationRequest request);

    BigDecimal getBalance(UUID walletId);
}
