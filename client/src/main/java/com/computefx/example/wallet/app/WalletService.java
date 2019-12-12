package com.computefx.example.wallet.app;

import com.computefx.example.wallet.Wallet;
import com.google.protobuf.Empty;
import reactor.core.publisher.Mono;

public interface WalletService {
    Mono<Empty> deposit(int userId, long amount, String currency);

    Mono<Empty> withdraw(int userId, long amount, String currency);

    Mono<Wallet.UserBalance> getBalance(int userId);
}
