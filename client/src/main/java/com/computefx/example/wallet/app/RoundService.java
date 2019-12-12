package com.computefx.example.wallet.app;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.google.protobuf.GeneratedMessageV3;
import reactor.core.publisher.Flux;

@Service
public class RoundService {
    final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final WalletService service;

    public RoundService(@Qualifier("block") WalletService service) {
        this.service = service;
    }

    public Flux<GeneratedMessageV3> roundA(int userId) {
        return Flux.concat(
                service.deposit(userId, 100L, "USD"),
                service.withdraw(userId, 200L, "USD"),
                service.deposit(userId, 100L, "EUR"),
                service.getBalance(userId),
                service.withdraw(userId, 100L, "USD"),
                service.getBalance(userId),
                service.withdraw(userId, 100L, "USD")
        );
    }

    public Flux<GeneratedMessageV3> roundB(int userId) {
        return Flux.concat(
                service.withdraw(userId, 100L, "GBP"),
                service.deposit(userId, 300L, "GBP"),
                service.withdraw(userId, 100L, "GBP"),
                service.withdraw(userId, 100L, "GBP"),
                service.withdraw(userId, 100L, "GBP")
        );
    }

    public Flux<GeneratedMessageV3> roundC(int userId) {
        return Flux.concat(
                service.getBalance(userId),
                service.deposit(userId, 100L, "USD"),
                service.deposit(userId, 100L, "USD"),
                service.withdraw(userId, 100L, "USD"),
                service.deposit(userId, 100L, "USD"),
                service.getBalance(userId),
                service.withdraw(userId, 200L, "USD"),
                service.getBalance(userId)
        );
    }

    public Flux<GeneratedMessageV3> selectRound(int userId) {
        final int i = random.nextInt(3);

        if (i > 1) {
            return roundC(userId);
        } else if (i > 0) {
            return roundB(userId);
        }
        return roundA(userId);
    }
}
