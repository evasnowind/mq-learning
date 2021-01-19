package com.prayerlaputa.example.primitive;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author chenglong.yu
 * created on 2020/9/3
 */
public abstract class AbstractTransfer {

    protected volatile int balance;

    public AbstractTransfer(int balance) {
        this.balance = balance;
    }

    public abstract void transfer(int amount);

    @Override
    public String toString() {
        return "Transfer{ balance=" + balance + '}';
    }

    public void testTransfer(int count, int amount) {
        IntStream.range(0, count)
                .parallel()
                .mapToObj((index) -> CompletableFuture.runAsync(() -> transfer(amount)))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}
