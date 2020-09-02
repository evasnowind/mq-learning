package com.prayerlaputa.keytech.network.netty.timeserver;

import java.util.Date;

/**
 * @author chenglong.yu
 * created on 2020/8/19
 */
public class UnixTime {


    private final long value;

    public UnixTime() {
        this(System.currentTimeMillis() / 1000L + 2208988800L);
    }

    public UnixTime(long value) {
        this.value = value;
    }

    public long value() {
        return value;
    }

    @Override
    public String toString() {
        return new Date((value() - 2208988800L) * 1000L).toString();
    }
}
