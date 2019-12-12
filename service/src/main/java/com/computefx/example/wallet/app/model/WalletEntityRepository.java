package com.computefx.example.wallet.app.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletEntityRepository extends JpaRepository<WalletEntity, Integer> {

    List<WalletEntity> findAllByUserIdAndAmountGreaterThan(Integer val, Long amount);

    Optional<WalletEntity> findByUserIdAndCurrency(Integer userId, String currency);
}
