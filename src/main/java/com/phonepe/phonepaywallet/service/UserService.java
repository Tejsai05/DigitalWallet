package com.phonepe.phonepaywallet.service;

import com.phonepe.phonepaywallet.dto.LoginRequest;
import com.phonepe.phonepaywallet.dto.UserRegistrationRequest;
import com.phonepe.phonepaywallet.entity.User;
import com.phonepe.phonepaywallet.entity.Wallet;
import com.phonepe.phonepaywallet.repository.UserRepository;
import com.phonepe.phonepaywallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public UserService(UserRepository userRepository, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public User registerUser(UserRegistrationRequest request) {
// Check if phone number already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }

// Check if UPI ID already exists
        if (userRepository.existsByUpiId(request.getUpiId())) {
            throw new RuntimeException("UPI ID already exists");
        }

// Create user
        User user = new User();
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setUpiId(request.getUpiId());
        user.setPin(request.getPin()); // In production, hash this!

        User savedUser = userRepository.save(user);

// Create wallet automatically on registration
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(BigDecimal.ZERO);
        walletRepository.save(wallet);

        return savedUser;
    }

    public User login(LoginRequest request) {
        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Phone Number Provided: [" + request.getPhoneNumber() + "]");
        System.out.println("PIN Provided: [" + request.getPin() + "]");
        
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> {
                    System.out.println("ERROR: User not found with phone: " + request.getPhoneNumber());
                    return new RuntimeException("Invalid phone number or PIN");
                });

        System.out.println("User Found: " + user.getName());
        System.out.println("Phone in DB: [" + user.getPhoneNumber() + "]");
        System.out.println("PIN in DB: [" + user.getPin() + "]");
        System.out.println("PIN Match: " + user.getPin().equals(request.getPin()));
        
// Validate PIN (in production, use proper password hashing)
        if (!user.getPin().equals(request.getPin())) {
            System.out.println("ERROR: PIN mismatch!");
            throw new RuntimeException("Invalid phone number or PIN");
        }

        System.out.println("LOGIN SUCCESSFUL");
        return user;
    }

    public User getUserProfile(String upiId) {
        return userRepository.findByUpiId(upiId)
                .orElseThrow(() -> new RuntimeException("User not found with UPI ID: " + upiId));
    }
}
