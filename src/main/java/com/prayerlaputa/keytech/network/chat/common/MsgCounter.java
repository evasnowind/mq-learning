package com.prayerlaputa.keytech.network.chat.common;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chenglong.yu
 * created on 2020/9/4
 */
public class MsgCounter {

    private static AtomicInteger counter = new AtomicInteger();
    private static volatile Long startTime;
    private static final Object LOCK = new Object();
    private static Map<Integer, Long> countTimeMapping = new LinkedHashMap<>(3);


    public static void start() {
        //DCL 双重锁检查方式实现单例
        if (null == startTime) {
            synchronized (LOCK) {
                if (null == startTime) {
                    startTime = System.currentTimeMillis();
                }
            }
        }
    }

    public static void count() {
        start();
        int count = counter.incrementAndGet();
        int finishCount = count / 6;

        if ((finishCount == MsgConst.COUNT_LEVEL_1 || finishCount == MsgConst.COUNT_LEVEL_2 || finishCount == MsgConst.COUNT_LEVEL_3) && count % 6 == 0) {
            long endTime = System.currentTimeMillis();
            long time = endTime - startTime;
            System.out.println("遍历" + finishCount + "次，花费:" + time + "ms");
            countTimeMapping.put(finishCount, time);
            System.out.println(countTimeMapping);
        }
    }
}
