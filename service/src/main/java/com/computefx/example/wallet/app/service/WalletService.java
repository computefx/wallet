package com.computefx.example.wallet.app.service;

import java.util.List;

import com.computefx.example.wallet.Wallet;
import com.computefx.example.wallet.app.model.WalletEntity;

public interface WalletService {
    List<Wallet.Money> balances(Wallet.User request);

    WalletEntity withdraw(Wallet.Order request);

    WalletEntity deposit(Wallet.Order request);
}
