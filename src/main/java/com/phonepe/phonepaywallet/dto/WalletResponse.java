package com.phonepe.phonepaywallet.dto;

import java.math.BigDecimal;

public class WalletResponse {
    private Long walletId;
    private String upiId;
    private BigDecimal balance;

    public WalletResponse() {
    }

    public WalletResponse(Long walletId, String upiId, BigDecimal balance) {
        this.walletId = walletId;
        this.upiId = upiId;
        this.balance = balance;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}

