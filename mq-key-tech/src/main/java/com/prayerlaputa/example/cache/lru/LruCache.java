package com.prayerlaputa.example.cache.lru;

/**
 * @author chenglong.yu
 * created on 2020/9/2
 */
public abstract class LruCache<K, V> implements Storage<K, V> {

    // 缓存容量
    protected final int capacity;
    // 低速存储，所有的数据都可以从这里读到
    protected final Storage lowSpeedStorage;

    public LruCache(int capacity, Storage lowSpeedStorage) {
        this.capacity = capacity;
        this.lowSpeedStorage = lowSpeedStorage;
    }
}
