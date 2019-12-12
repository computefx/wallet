package com.computefx.example.wallet.app.service;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class WalletException extends RuntimeException {

    private final Status status;

    public WalletException(Status status, String message) {
        super(message);
        this.status = status;
    }

    public StatusRuntimeException asStatusException() {
        return status.withDescription(getMessage()).asRuntimeException();
    }
}
