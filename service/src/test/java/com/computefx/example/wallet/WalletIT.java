package com.computefx.example.wallet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import io.grpc.StatusRuntimeException;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import net.devh.boot.grpc.client.inject.GrpcClient;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class WalletIT extends WithMysqlContainer {

    @GrpcClient("wallet-grpc-server")
    WalletServiceGrpc.WalletServiceBlockingStub blockingStub;

    Wallet.User user = Wallet.User.newBuilder().setUserId(1).build();

    Wallet.Order order = Wallet.Order.newBuilder().setUser(user).build();

    @Test
    void shouldWithdrawAndReturnInsufficientFunds() {
        assertThatThrownBy(() -> blockingStub.withdraw(Wallet.Order.newBuilder(order).setMoney(mapToMoney(Tuple.of(100, "GBP"))).build()))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("insufficient funds");
    }

    @Test
    void shouldWithdrawAndReturnUnknownCurrency() {
        assertThatThrownBy(() -> blockingStub.withdraw(Wallet.Order.newBuilder(order).setMoney(Wallet.Money.newBuilder().setCurrencyValue(4).setAmount(100).build()).build()))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("Unknown currency");
    }

    @Test
    void shouldDepositAndReturnUnknownCurrency() {
        assertThatThrownBy(() -> blockingStub.deposit(Wallet.Order.newBuilder(order).setMoney(Wallet.Money.newBuilder().setCurrencyValue(4).setAmount(100).build()).build()))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("Unknown currency");
    }

    @Test
    void testScenario() {
        withdraw(Tuple.of(200, "USD"),"insufficient funds");
        deposit(Tuple.of(100, "USD"));
        balance(Tuple.of(100, "USD"));

        withdraw(Tuple.of(200, "USD"),"insufficient funds");
        deposit(Tuple.of(100, "EUR"));
        balance(Tuple.of(100, "USD"), Tuple.of(100, "EUR"));

        withdraw(Tuple.of(200, "USD"),"insufficient funds");
        deposit(Tuple.of(100, "USD"));
        balance(Tuple.of(200, "USD"), Tuple.of(100, "EUR"));

        withdraw(Tuple.of(200, "USD"));
        balance(Tuple.of(100, "EUR"));
        withdraw(Tuple.of(200, "USD"),"insufficient funds");
    }

    private void withdraw(Tuple2<Integer, String> val, String error) {
        assertThatThrownBy(() -> blockingStub.withdraw(Wallet.Order.newBuilder(order).setMoney(mapToMoney(val)).build()))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining(error);
    }

    private void withdraw(Tuple2<Integer, String> val) {
        assertThatCode(() -> blockingStub.withdraw(Wallet.Order.newBuilder(order).setMoney(mapToMoney(val)).build()))
                .doesNotThrowAnyException();
    }

    private void deposit(Tuple2<Integer, String> val) {
        assertThatCode(() -> blockingStub.deposit(Wallet.Order.newBuilder(order).setMoney(mapToMoney(val)).build()))
                .doesNotThrowAnyException();
    }

    private void balance(Tuple2<Integer, String>... values) {
        final Wallet.UserBalance balance = blockingStub.balance(user);

        assertThat(balance.getBalancesList()).containsOnlyElementsOf(Stream.of(values).map(this::mapToMoney).collect(Collectors.toList()));
    }

    private Wallet.Money mapToMoney(Tuple2<Integer, String> val) {
        return Wallet.Money.newBuilder().setAmount(val._1).setCurrency(Wallet.Money.CurrencyCode.valueOf(val._2)).build();
    }
}
