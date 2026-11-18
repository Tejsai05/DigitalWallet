package com.phonepe.phonepaywallet.controller;

import com.phonepe.phonepaywallet.dto.ApiResponse;
import com.phonepe.phonepaywallet.dto.SendMoneyRequest;
import com.phonepe.phonepaywallet.dto.TransactionResponse;
import com.phonepe.phonepaywallet.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transaction")
@CrossOrigin(origins = "*")
@Tag(name = "Transaction Operations", description = "Core APIs for sending money and viewing transaction history")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Send money",
            description = "Send money from sender to receiver with idempotency protection. Validates UPI IDs, checks balance, deducts from sender, adds to receiver, and saves transaction record with complete audit trail.")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendMoney(@Valid @RequestBody SendMoneyRequest request) {
        try {
            // Generate idempotency key if not provided by client
            if (request.getIdempotencyKey() == null || request.getIdempotencyKey().trim().isEmpty()) {
                request.setIdempotencyKey(UUID.randomUUID().toString());
            }
            
            System.out.println("SEND_MONEY_REQUEST: Idempotency Key=" + request.getIdempotencyKey());
            TransactionResponse transaction = transactionService.sendMoney(request);
            return ResponseEntity.ok(new ApiResponse(true, "Money sent successfully", transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @Operation(summary = "Get transaction history",
            description = "Retrieve all sent and received transactions for a given UPI ID")
    @GetMapping("/history/{upiId}")
    public ResponseEntity<ApiResponse> getTransactionHistory(@PathVariable String upiId) {
        try {
            List<TransactionResponse> transactions = transactionService.getTransactionHistory(upiId);
            return ResponseEntity.ok(new ApiResponse(true, "Transaction history retrieved", transactions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
