package com.phonepe.phonepaywallet.service;

import com.phonepe.phonepaywallet.dto.SendMoneyRequest;
import com.phonepe.phonepaywallet.dto.TransactionResponse;
import com.phonepe.phonepaywallet.entity.IdempotencyKey;
import com.phonepe.phonepaywallet.entity.Transaction;
import com.phonepe.phonepaywallet.entity.Transaction.TransactionStatus;
import com.phonepe.phonepaywallet.entity.User;
import com.phonepe.phonepaywallet.entity.Wallet;
import com.phonepe.phonepaywallet.repository.IdempotencyKeyRepository;
import com.phonepe.phonepaywallet.repository.TransactionRepository;
import com.phonepe.phonepaywallet.repository.UserRepository;
import com.phonepe.phonepaywallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, WalletRepository walletRepository, IdempotencyKeyRepository idempotencyKeyRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
    }

    /**
     * Core Transaction Logic with Idempotency & Audit Logging:
     * 1. Check for duplicate request using idempotency key
     * 2. Validate sender & receiver UPI
     * 3. Check sender wallet balance
     * 4. Deduct amount from sender
     * 5. Add amount to receiver
     * 6. Save transaction record with audit trail
     * 7. Store idempotency key for future duplicate detection
     */
    @Transactional
    public TransactionResponse sendMoney(SendMoneyRequest request) {
        StringBuilder auditLog = new StringBuilder();
        String idempotencyKey = request.getIdempotencyKey();
        
        // Generate idempotency key if not provided
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            idempotencyKey = UUID.randomUUID().toString();
            auditLog.append("Generated idempotency key: ").append(idempotencyKey).append(" | ");
        }
        
        // 1. Check if this transaction was already processed
        java.util.Optional<IdempotencyKey> existingKey = idempotencyKeyRepository.findByKeyValue(idempotencyKey);
        if (existingKey.isPresent()) {
            auditLog.append("Duplicate request detected with idempotency key: ").append(idempotencyKey).append(" | ");
            System.out.println("IDEMPOTENCY: Duplicate transaction prevented for key: " + idempotencyKey);
            
            Transaction existingTransaction = transactionRepository.findById(existingKey.get().getTransactionId())
                    .orElseThrow(() -> new RuntimeException("Original transaction not found"));
            
            existingTransaction.setRetryCount((existingTransaction.getRetryCount() != null ? existingTransaction.getRetryCount() : 0) + 1);
            existingTransaction.setAuditLog(existingTransaction.getAuditLog() + " | " + auditLog.toString());
            transactionRepository.save(existingTransaction);
            
            return mapToTransactionResponse(existingTransaction);
        }
        
        try {
            // 2. Validate sender UPI ID
            auditLog.append("Validating sender UPI: ").append(request.getSenderUpi()).append(" | ");
            User sender = userRepository.findByUpiId(request.getSenderUpi())
                    .orElseThrow(() -> new RuntimeException("Sender UPI ID not found: " + request.getSenderUpi()));
            
            // 3. Validate receiver UPI ID
            auditLog.append("Validating receiver UPI: ").append(request.getReceiverUpi()).append(" | ");
            User receiver = userRepository.findByUpiId(request.getReceiverUpi())
                    .orElseThrow(() -> new RuntimeException("Receiver UPI ID not found: " + request.getReceiverUpi()));
            
            // Prevent self-transfer
            if (sender.getUpiId().equals(receiver.getUpiId())) {
                throw new RuntimeException("Cannot send money to yourself");
            }
            
            // Get wallets
            Wallet senderWallet = walletRepository.findByUserUpiId(request.getSenderUpi())
                    .orElseThrow(() -> new RuntimeException("Sender wallet not found"));
            
            Wallet receiverWallet = walletRepository.findByUserUpiId(request.getReceiverUpi())
                    .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));
            
            // 4. Check sender wallet balance
            auditLog.append("Checking balance: ").append(senderWallet.getBalance()).append(" vs amount: ").append(request.getAmount()).append(" | ");
            if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
                auditLog.append("Insufficient balance. Available: ").append(senderWallet.getBalance()).append(" | ");
                
                Transaction failedTransaction = createTransaction(
                        request.getSenderUpi(),
                        request.getReceiverUpi(),
                        request.getAmount(),
                        TransactionStatus.FAILED,
                        "Insufficient balance. Available: " + senderWallet.getBalance(),
                        idempotencyKey,
                        auditLog.toString()
                );
                transactionRepository.save(failedTransaction);
                throw new RuntimeException("Insufficient balance. Available: " + senderWallet.getBalance());
            }
            
            // Validate minimum amount
            if (request.getAmount().compareTo(BigDecimal.ONE) < 0) {
                throw new RuntimeException("Amount must be at least 1");
            }
            
            // 5. Deduct amount from sender
            auditLog.append("Deducting ").append(request.getAmount()).append(" from sender | ");
            senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));
            walletRepository.save(senderWallet);
            
            // 6. Add amount to receiver
            auditLog.append("Adding ").append(request.getAmount()).append(" to receiver | ");
            receiverWallet.setBalance(receiverWallet.getBalance().add(request.getAmount()));
            walletRepository.save(receiverWallet);
            
            // 7. Save transaction record as SUCCESS
            auditLog.append("Transaction completed successfully | ");
            Transaction transaction = createTransaction(
                    request.getSenderUpi(),
                    request.getReceiverUpi(),
                    request.getAmount(),
                    TransactionStatus.SUCCESS,
                    null,
                    idempotencyKey,
                    auditLog.toString()
            );
            Transaction savedTransaction = transactionRepository.save(transaction);
            
            // Store idempotency key
            IdempotencyKey key = new IdempotencyKey(idempotencyKey, savedTransaction.getId(), LocalDateTime.now(), 
                    "Transaction ID: " + savedTransaction.getId());
            idempotencyKeyRepository.save(key);
            
            System.out.println("TRANSACTION SUCCESS: " + savedTransaction.getId() + " with key: " + idempotencyKey);
            return mapToTransactionResponse(savedTransaction);
            
        } catch (Exception e) {
            auditLog.append("Transaction failed with error: ").append(e.getMessage()).append(" | ");
            
            // Save failed transaction with error details
            Transaction failedTransaction = createTransaction(
                    request.getSenderUpi(),
                    request.getReceiverUpi(),
                    request.getAmount(),
                    TransactionStatus.FAILED,
                    e.getMessage(),
                    idempotencyKey,
                    auditLog.toString()
            );
            Transaction savedFailedTransaction = transactionRepository.save(failedTransaction);
            
            // Store idempotency key even for failed transaction to prevent retry
            IdempotencyKey key = new IdempotencyKey(idempotencyKey, savedFailedTransaction.getId(), LocalDateTime.now(), 
                    "Failed Transaction ID: " + savedFailedTransaction.getId());
            idempotencyKeyRepository.save(key);
            
            System.out.println("TRANSACTION FAILED: " + e.getMessage());
            throw new RuntimeException("Transaction failed: " + e.getMessage());
        }
    }

    public List<TransactionResponse> getTransactionHistory(String upiId) {
// Validate UPI ID exists
        userRepository.findByUpiId(upiId)
                .orElseThrow(() -> new RuntimeException("UPI ID not found: " + upiId));

        List<Transaction> transactions = transactionRepository
                .findBySenderUpiOrReceiverUpiOrderByDateDesc(upiId, upiId);

        return transactions.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    private Transaction createTransaction(String senderUpi, String receiverUpi,
                                          BigDecimal amount, TransactionStatus status,
                                          String errorMessage, String idempotencyKey, String auditLog) {
        Transaction transaction = new Transaction();
        transaction.setSenderUpi(senderUpi);
        transaction.setReceiverUpi(receiverUpi);
        transaction.setAmount(amount);
        transaction.setDate(LocalDateTime.now());
        transaction.setStatus(status);
        transaction.setErrorMessage(errorMessage);
        transaction.setIdempotencyKey(idempotencyKey);
        transaction.setAuditLog(auditLog);
        transaction.setRetryCount(0);
        return transaction;
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getSenderUpi(),
                transaction.getReceiverUpi(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getStatus()
        );
    }
}
