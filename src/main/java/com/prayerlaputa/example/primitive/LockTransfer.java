package com.prayerlaputa.example.primitive;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author chenglong.yu
 * created on 2020/9/3
 */
public class LockTransfer extends AbstractTransfer {

    private static final Lock LOCK = new ReentrantLock();

    public LockTransfer(int balance) {
        super(balance);
    }

    @Override
    public void transfer(int amount) {
        LOCK.lock();
        try {
            balance += amount;
        } finally {
            LOCK.unlock();
        }
    }

    @Override
    public String toString() {
        return "balance = " + balance;
    }

    public static void main(String[] args) {
        LockTransfer lockTransfer = new LockTransfer(0);
        lockTransfer.testTransfer(10000, 1);
        System.out.println("lock transfer " + lockTransfer);
    }
}
