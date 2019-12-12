package com.computefx.example.wallet.app.grpc;

import java.util.List;

import com.computefx.example.wallet.Wallet;
import com.computefx.example.wallet.WalletServiceGrpc;
import com.computefx.example.wallet.app.service.WalletException;
import com.computefx.example.wallet.app.service.WalletService;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.vavr.control.Validation;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
class GrpcWalletService extends WalletServiceGrpc.WalletServiceImplBase {

    private final Empty empty = Empty.newBuilder().build();

    private final WalletService walletService;
    private final MoneyValidator moneyValidator;

    GrpcWalletService(WalletService walletService,
                      MoneyValidator moneyValidator) {
        this.walletService = walletService;
        this.moneyValidator = moneyValidator;
    }

    @Override
    public void deposit(Wallet.Order request, StreamObserver<Empty> observer) {

        try {
            validate(request.getMoney());
            walletService.deposit(request);
            observer.onNext(empty);
            observer.onCompleted();
        } catch (WalletException e) {
            observer.onError(e.asStatusException());
        } catch (Exception e) {
            observer.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void withdraw(Wallet.Order request, StreamObserver<Empty> observer) {

        try {
            validate(request.getMoney());
            walletService.withdraw(request);
            observer.onNext(empty);
            observer.onCompleted();
        } catch (WalletException e) {
            observer.onError(e.asStatusException());
        } catch (Exception e) {
            observer.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void balance(Wallet.User request, StreamObserver<Wallet.UserBalance> observer) {
        try {
            final List<Wallet.Money> balances = walletService.balances(request);
            observer.onNext(Wallet.UserBalance.newBuilder().addAllBalances(balances).build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    private void validate(Wallet.Money money) {
        final Validation<String, Wallet.Money> validation = moneyValidator.validate(money);

        if (validation.isInvalid()) {
            throw new WalletException(Status.INVALID_ARGUMENT, validation.getError());
        }
    }
}
