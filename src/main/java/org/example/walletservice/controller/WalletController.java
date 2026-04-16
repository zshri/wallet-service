package org.example.walletservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.walletservice.dto.WalletOperationRequest;
import org.example.walletservice.dto.WalletResponse;
import org.example.walletservice.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/wallet")
    public ResponseEntity<Void> processOperation(@Valid @RequestBody WalletOperationRequest request) {
        walletService.processOperation(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<WalletResponse> getBalance(@PathVariable UUID walletId) {
        BigDecimal balance = walletService.getBalance(walletId);
        WalletResponse response = new WalletResponse(walletId, balance);
        return ResponseEntity.ok(response);
    }
}
