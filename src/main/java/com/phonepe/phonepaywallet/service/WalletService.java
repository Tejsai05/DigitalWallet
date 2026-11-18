package com.phonepe.phonepaywallet.service;

import com.phonepe.phonepaywallet.dto.AddMoneyRequest;
import com.phonepe.phonepaywallet.dto.WalletResponse;
import com.phonepe.phonepaywallet.entity.User;
import com.phonepe.phonepaywallet.entity.Wallet;
import com.phonepe.phonepaywallet.repository.UserRepository;
import com.phonepe.phonepaywallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

        public WalletService(WalletRepository walletRepository, UserRepository userRepository) {
                this.walletRepository = walletRepository;
                this.userRepository = userRepository;
        }

    public WalletResponse getWalletBalance(String upiId) {
        User user = userRepository.findByUpiId(upiId)
                .orElseThrow(() -> new RuntimeException("User not found with UPI ID: " + upiId));

        Wallet wallet = walletRepository.findByUserUpiId(upiId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for UPI ID: " + upiId));

        return new WalletResponse(wallet.getId(), user.getUpiId(), wallet.getBalance());
    }

    @Transactional
    public WalletResponse addMoney(AddMoneyRequest request) {
        User user = userRepository.findByUpiId(request.getUpiId())
                .orElseThrow(() -> new RuntimeException("User not found with UPI ID: " + request.getUpiId()));

        Wallet wallet = walletRepository.findByUserUpiId(request.getUpiId())
                .orElseThrow(() -> new RuntimeException("Wallet not found for UPI ID: " + request.getUpiId()));

// Validate amount
        if (request.getAmount().compareTo(BigDecimal.ONE) < 0) {
            throw new RuntimeException("Amount must be at least 1");
        }

// Add money (dummy operation - in real app, integrate with payment gateway)
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        Wallet updatedWallet = walletRepository.save(wallet);

        return new WalletResponse(updatedWallet.getId(), user.getUpiId(), updatedWallet.getBalance());
    }
}
