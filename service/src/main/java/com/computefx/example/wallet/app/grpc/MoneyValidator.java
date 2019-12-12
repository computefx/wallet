package com.computefx.example.wallet.app.grpc;

import org.springframework.stereotype.Component;
import com.computefx.example.wallet.Wallet;
import io.vavr.control.Validation;

@Component
public class MoneyValidator {

    public Validation<String, Wallet.Money> validate(Wallet.Money val) {
        if (val == null || val.getCurrency() == Wallet.Money.CurrencyCode.UNRECOGNIZED) {
            return Validation.invalid("Unknown currency");
        }
        return Validation.valid(val);
    }
}
