package com.computefx.example.wallet.app;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.computefx.example.wallet.Wallet;
import com.computefx.example.wallet.WalletServiceGrpc;
import com.google.protobuf.Empty;
import net.devh.boot.grpc.client.inject.GrpcClient;
import reactor.core.publisher.Mono;

@Qualifier("block")
@Service
class BlockWalletService extends AbstractWalletService {

    @GrpcClient("wallet-grpc-server")
    WalletServiceGrpc.WalletServiceBlockingStub blockingStub;

    @Override
    public Mono<Empty> deposit(int userId, long amount, String currency) {
        return Mono.fromCallable(() -> blockingStub.deposit(order(userId, amount, currency)))
                .doOnError(e -> logger.info(e.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }

    @Override
    public Mono<Empty> withdraw(int userId, long amount, String currency) {
        return Mono.fromCallable(() -> blockingStub.withdraw(order(userId, amount, currency)))
                .doOnError(e -> logger.info(e.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }

    @Override
    public Mono<Wallet.UserBalance> getBalance(int userId) {
        return Mono.fromCallable(() -> blockingStub.balance(Wallet.User.newBuilder().setUserId(userId).build()))
                .doOnError(e -> logger.info(e.getMessage()));
    }
}
