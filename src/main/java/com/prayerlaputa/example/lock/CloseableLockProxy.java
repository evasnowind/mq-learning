package com.prayerlaputa.example.lock;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author chenglong.yu
 * created on 2020/9/3
 */
public class CloseableLockProxy implements Closeable {

    private static final ReentrantLock LOCK = new ReentrantLock();

    @Override
    public void close() {
        LOCK.unlock();
        System.out.println(LOCK + "锁已释放");
    }

    public void lock() {
        LOCK.lock();
        System.out.println(LOCK + "已上锁");
    }

    public static void main(String[] args) {
        /*
        由于实现了Closeable（继承自AutoCloseable）接口，可以使用JDK 1.7支持的try-with-resource语法，
        当try(){}结束、需要关闭资源时，将自动调用close方法。
         */
        IntStream.range(0, 5)
                .mapToObj(index -> CompletableFuture.runAsync(() -> {
                    try(CloseableLockProxy lockProxy = new CloseableLockProxy()) {
                        lockProxy.lock();
                        System.out.println("task running: index=" + index);
                    }
                }))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}
