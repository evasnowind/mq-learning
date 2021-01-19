package com.prayerlaputa.example.cache.lru;

/**
 * @author chenglong.yu
 * created on 2020/9/2
 */
public interface Storage<K, V> {

    /** * 根据提供的key来访问数据 * @param key 数据Key * @return 数据值 */
    V get(K key);
}
