package com.prayerlaputa.example.primitive;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chenglong.yu
 * created on 2020/9/3
 */
public class AtomicIntegerTransfer extends AbstractTransfer {

    private AtomicInteger balance;

    public AtomicIntegerTransfer(int balance) {
        super(balance);
        this.balance = new AtomicInteger(balance);
    }

    @Override
    public void transfer(int amount) {
        balance.addAndGet(amount);
    }

    @Override
    public String toString() {
        return "balance = " + balance;
    }

    public static void main(String[] args) {
        AtomicIntegerTransfer transfer = new AtomicIntegerTransfer(0);
        transfer.testTransfer(10000, 1);
        System.out.println("AtomicIntegerTransfer " + transfer);
    }
}
