
极客时间-消息队列高手课

作业题
实现一个采用 LRU 置换算法的缓存。

```
/**
 * KV存储抽象
 */
public interface Storage<K,V> {
    /**
     * 根据提供的key来访问数据
     * @param key 数据Key
     * @return 数据值
     */
    V get(K key);
}

/**
 * LRU缓存。你需要继承这个抽象类来实现LRU缓存。
 * @param <K> 数据Key
 * @param <V> 数据值
 */
public abstract class LruCache<K, V> implements Storage<K,V>{
    // 缓存容量
    protected final int capacity;
    // 低速存储，所有的数据都可以从这里读到
    protected final Storage<K,V> lowSpeedStorage;

    public LruCache(int capacity, Storage<K,V> lowSpeedStorage) {
        this.capacity = capacity;
        this.lowSpeedStorage = lowSpeedStorage;
    }
}
```