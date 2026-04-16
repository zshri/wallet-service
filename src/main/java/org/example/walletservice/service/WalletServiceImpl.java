package org.example.walletservice.service;

import org.example.walletservice.dto.OperationType;
import org.example.walletservice.dto.WalletOperationRequest;
import org.example.walletservice.entity.Wallet;
import org.example.walletservice.exception.InsufficientFundsException;
import org.example.walletservice.exception.WalletNotFoundException;
import org.example.walletservice.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    @Transactional
    public void processOperation(WalletOperationRequest request) {
        Wallet wallet = walletRepository.findByIdForUpdate(request.getWalletId())
                .orElseThrow(() -> new WalletNotFoundException(request.getWalletId()));

        BigDecimal newBalance;
        if (request.getOperationType() == OperationType.DEPOSIT) {
            newBalance = wallet.getBalance().add(request.getAmount());
        } else {
            if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientFundsException(request.getWalletId(), request.getAmount(), wallet.getBalance());
            }
            newBalance = wallet.getBalance().subtract(request.getAmount());
        }
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID walletId) {
        return walletRepository.findById(walletId)
                .map(Wallet::getBalance)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
    }
}
