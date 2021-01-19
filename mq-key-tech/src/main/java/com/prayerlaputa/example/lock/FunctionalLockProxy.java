package com.prayerlaputa.example.lock;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author chenglong.yu
 * created on 2020/9/3
 */
public class FunctionalLockProxy {
    private static final Lock LOCK = new ReentrantLock();


    public <T> T exec(Supplier<T> supplier) {
        T result;
        LOCK.lock();
        System.out.println(LOCK + "已上锁");
        try {
            result = supplier.get();
            System.out.println(result);
        } finally {
            LOCK.unlock();
            System.out.println(LOCK + "锁已释放");
        }

        return result;
    }


    public static void main(String[] args) {
        IntStream.range(0,5)
                .mapToObj(index -> CompletableFuture.runAsync(() -> {
                    FunctionalLockProxy proxy = new FunctionalLockProxy();
                    proxy.exec(() -> "task runing:" + index);
                }))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}
