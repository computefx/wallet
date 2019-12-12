package com.computefx.example.wallet;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.computefx.example.wallet.app.RoundService;
import com.google.common.base.Stopwatch;
import picocli.CommandLine;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class WalletClientApplication implements CommandLineRunner, Runnable {
    private static final Logger logger = LoggerFactory.getLogger(WalletClientApplication.class);

    @Autowired
    RoundService roundService;

    @CommandLine.Option(names = {"-u"}, description = "Users", required = true)
    Integer users;
    @CommandLine.Option(names = {"-c"}, description = "Requests per user", required = true)
    Integer requestPerUser;
    @CommandLine.Option(names = {"-r"}, description = "Rounds", required = true)
    Integer rounds;

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(WalletClientApplication.class, args)));
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            new CommandLine(this).execute(args);
        } finally {
            Schedulers.shutdownNow();
        }
    }

    @Override
    public void run() {
        AtomicInteger counter = new AtomicInteger(0);
        Stopwatch watch = Stopwatch.createStarted();
        Flux.range(1, users)
                .parallel()
                .runOn(Schedulers.elastic())
                .flatMap(userId -> Flux.range(1, rounds)
                        .parallel()
                        .runOn(requestPerUser > 1 ? Schedulers.newParallel(UUID.randomUUID().toString(), requestPerUser) : Schedulers.immediate())
                        .flatMap(round -> roundService.selectRound(userId)
                                .doOnEach(i -> counter.incrementAndGet()))
                        .sequential()
                        .ignoreElements()
                )
                .sequential()
                .ignoreElements()
                .doFinally(endType -> logger.info("Time taken : {} milliseconds.", watch.elapsed(TimeUnit.MILLISECONDS)))
                .block();

        logger.info("Total (success&error) request count {}", counter.get());

        watch.stop();
    }
}
