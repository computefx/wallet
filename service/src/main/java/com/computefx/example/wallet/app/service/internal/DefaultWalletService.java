package com.computefx.example.wallet.app.service.internal;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.computefx.example.wallet.Wallet;
import com.computefx.example.wallet.app.model.WalletEntity;
import com.computefx.example.wallet.app.model.WalletEntityRepository;
import com.computefx.example.wallet.app.service.WalletException;
import com.computefx.example.wallet.app.service.WalletService;
import io.grpc.Status;

@Service
class DefaultWalletService implements WalletService {

    private final WalletEntityRepository repository;

    DefaultWalletService(WalletEntityRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Wallet.Money> balances(Wallet.User request) {
        return repository.findAllByUserIdAndAmountGreaterThan(request.getUserId(), 0L).stream()
                .map(w -> Wallet.Money.newBuilder().setAmount(w.getAmount() / 100L).setCurrency(Wallet.Money.CurrencyCode.valueOf(w.getCurrency())).build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public WalletEntity withdraw(Wallet.Order request) {
        final Wallet.Money money = request.getMoney();
        final Wallet.User user = request.getUser();

        final WalletEntity entity = repository.findByUserIdAndCurrency(user.getUserId(), money.getCurrency().name())
                .orElseGet(() -> new WalletEntity(user.getUserId(), 0L, money.getCurrency().name()));

        final long amount = money.getAmount() * 100L;

        if (entity.getAmount() < amount) {
            throw new WalletException(Status.FAILED_PRECONDITION, "insufficient funds");
        }

        return repository.save(entity.subtractAmount(amount));
    }

    @Transactional
    @Override
    public WalletEntity deposit(Wallet.Order request) {
        final Wallet.Money money = request.getMoney();
        final Wallet.User user = request.getUser();

        final WalletEntity entity = repository.findByUserIdAndCurrency(user.getUserId(), money.getCurrency().name())
                .orElseGet(() -> new WalletEntity(user.getUserId(), 0L, money.getCurrency().name()));

        return repository.save(entity.addAmount(money.getAmount() * 100L));
    }

}
