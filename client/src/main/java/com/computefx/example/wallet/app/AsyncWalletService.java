package com.computefx.example.wallet.app;

import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.computefx.example.wallet.Wallet;
import com.computefx.example.wallet.WalletServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import reactor.core.publisher.Mono;

@Service
class AsyncWalletService extends AbstractWalletService {

    @GrpcClient("wallet-grpc-server")
    WalletServiceGrpc.WalletServiceStub walletServiceStub;

    @Override
    public Mono<Empty> deposit(int userId, long amount, String currency) {
        return oneToOne(order(userId, amount, currency), walletServiceStub::deposit)
                .doOnError(e -> logger.info(e.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }

    @Override
    public Mono<Empty> withdraw(int userId, long amount, String currency) {
        return oneToOne(order(userId, amount, currency), walletServiceStub::withdraw)
                .doOnError(e -> logger.info(e.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }

    @Override
    public Mono<Wallet.UserBalance> getBalance(int userId) {
        return oneToOne(Wallet.User.newBuilder().setUserId(userId).build(), walletServiceStub::balance)
                .doOnError(e -> logger.info(e.getMessage()));
    }

    /**
     * Implements a unary → unary call using {@link Mono} → {@link Mono}.
     */
    private static <T, R> Mono<R> oneToOne(T request, BiConsumer<T, StreamObserver<R>> delegate) {
        return Mono.create(emitter -> delegate.accept(request, new StreamObserver<>() {
            @Override
            public void onNext(R value) {
                emitter.success(value);
            }

            @Override
            public void onError(Throwable t) {
                emitter.error(t);
            }

            @Override
            public void onCompleted() {
                // do nothing
            }
        }));
    }

}
