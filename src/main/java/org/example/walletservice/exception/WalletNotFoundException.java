package org.example.walletservice.exception;

import java.util.UUID;

public class WalletNotFoundException extends RuntimeException {

    public WalletNotFoundException(UUID walletId) {
        super(String.format("Wallet with id %s not found", walletId));
    }
}