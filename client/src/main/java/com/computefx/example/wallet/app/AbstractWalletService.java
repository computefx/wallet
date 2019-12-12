package com.computefx.example.wallet.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.computefx.example.wallet.Wallet;

public abstract class AbstractWalletService implements WalletService {
    static final Logger logger = LoggerFactory.getLogger(AbstractWalletService.class);

    protected Wallet.Order order(int userId, long amount, String currency) {
        return Wallet.Order.newBuilder()
                .setUser(Wallet.User.newBuilder().setUserId(userId).build())
                .setMoney(Wallet.Money.newBuilder().setAmount(amount).setCurrency(Wallet.Money.CurrencyCode.valueOf(currency)).build())
                .build();
    }

}
