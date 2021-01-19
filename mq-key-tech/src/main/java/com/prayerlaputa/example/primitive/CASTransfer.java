package com.prayerlaputa.example.primitive;


import sun.misc.Unsafe;

import java.io.File;
import java.lang.reflect.Field;

/**
 * @author chenglong.yu
 * created on 2020/9/3
 */
public class CASTransfer extends AbstractTransfer {

    private static Unsafe UNSAFE;
    private static long VALUE_OFFSET;
    public static final String THE_UNSAFE_STR = "theUnsafe";

    static {

        Field field = null;
        try {
            field = Unsafe.class.getDeclaredField(THE_UNSAFE_STR);
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);
            VALUE_OFFSET = UNSAFE.objectFieldOffset(AbstractTransfer.class.getDeclaredField("balance"));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public CASTransfer(int balance) {
        super(balance);
    }

    @Override
    public void transfer(int amount) {
        /*
        此处可以看一下AtomicInteger的getAndAddInt源码，下面这个代码就是抄的JDK
         */
        int oldValue;
        do {
            oldValue = UNSAFE.getIntVolatile(this, VALUE_OFFSET);
        } while(!UNSAFE.compareAndSwapInt(this, VALUE_OFFSET, oldValue, oldValue + amount));
    }

    @Override
    public String toString() {
        return "balance = " + balance;
    }

    public static void main(String[] args) {
        CASTransfer casTransfer = new CASTransfer(0);
        casTransfer.testTransfer(10000, 1);
        System.out.println("cas transfer " + casTransfer);
    }
}
