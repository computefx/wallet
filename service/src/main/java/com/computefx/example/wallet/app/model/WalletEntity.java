package com.computefx.example.wallet.app.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "wallets")
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    @Column(name = "user_id")
    private Integer userId;

    private Long amount; // cents

    private String currency;

    protected WalletEntity() {
        // jpa
    }

    public WalletEntity(Integer userId, Long amount, String currency) {
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
    }

    public Integer getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    public Integer getUserId() {
        return userId;
    }

    public Long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public WalletEntity addAmount(Long val) {
        this.amount += val;
        return this;
    }

    public WalletEntity subtractAmount(Long val) {
        this.amount -= val;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletEntity walletEntity = (WalletEntity) o;
        return Objects.equals(userId, walletEntity.userId) &&
                Objects.equals(currency, walletEntity.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, currency);
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", version=" + version +
                ", userId=" + userId +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
    }
}
