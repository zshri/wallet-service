package org.example.walletservice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.walletservice.entity.Wallet;
import org.example.walletservice.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(properties = {
        "server.port=0",
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class WalletControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID walletId;

    @BeforeEach
    void setUp() {
        walletRepository.deleteAll();
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("1000.00"));
        wallet = walletRepository.save(wallet);
        walletId = wallet.getId();
    }

    @Test
    void depositShouldIncreaseBalance() throws Exception {
        String request = """
                {
                    "walletId": "%s",
                    "operationType": "DEPOSIT",
                    "amount": 500.00
                }
                """.formatted(walletId);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500.00));
    }

    @Test
    void withdrawShouldDecreaseBalance() throws Exception {
        String request = """
                {
                    "walletId": "%s",
                    "operationType": "WITHDRAW",
                    "amount": 300.00
                }
                """.formatted(walletId);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(700.00));
    }

    @Test
    void withdrawMoreThanBalanceShouldReturnBadRequest() throws Exception {
        String request = """
                {
                    "walletId": "%s",
                    "operationType": "WITHDRAW",
                    "amount": 2000.00
                }
                """.formatted(walletId);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_FUNDS"));
    }

    @Test
    void nonExistentWalletShouldReturnNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(get("/api/v1/wallets/{walletId}", randomId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WALLET_NOT_FOUND"));
    }

    @Test
    void invalidJsonShouldReturnBadRequest() throws Exception {
        String invalidJson = "{ invalid }";
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_JSON"));
    }

    @Test
    void negativeAmountShouldReturnValidationError() throws Exception {
        String request = """
                {
                    "walletId": "%s",
                    "operationType": "DEPOSIT",
                    "amount": -100
                }
                """.formatted(walletId);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
