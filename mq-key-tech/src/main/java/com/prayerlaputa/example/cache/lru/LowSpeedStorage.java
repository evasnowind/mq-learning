package com.prayerlaputa.example.cache.lru;

import java.util.Date;

/**
 * @author chenglong.yu
 * created on 2020/9/3
 */
public class LowSpeedStorage<K, V> implements Storage {

    @Override
    public Object get(Object key) {
        return new Date().toString() + key;
    }
}
