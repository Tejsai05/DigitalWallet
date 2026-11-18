package com.phonepe.phonepaywallet.controller;

import com.phonepe.phonepaywallet.dto.AddMoneyRequest;
import com.phonepe.phonepaywallet.dto.ApiResponse;
import com.phonepe.phonepaywallet.dto.WalletResponse;
import com.phonepe.phonepaywallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
@Tag(name = "Wallet Management", description = "APIs for wallet balance management and adding money")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @Operation(summary = "Get wallet balance",
            description = "Retrieve wallet balance for a given UPI ID")
    @GetMapping("/{upiId}")
    public ResponseEntity<ApiResponse> getWalletBalance(@PathVariable String upiId) {
        try {
            WalletResponse wallet = walletService.getWalletBalance(upiId);
            return ResponseEntity.ok(new ApiResponse(true, "Wallet balance retrieved", wallet));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @Operation(summary = "Add money to wallet",
            description = "Add money to wallet using UPI ID (dummy operation for demo)")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addMoney(@Valid @RequestBody AddMoneyRequest request) {
        try {
            WalletResponse wallet = walletService.addMoney(request);
            return ResponseEntity.ok(new ApiResponse(true, "Money added successfully", wallet));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}

