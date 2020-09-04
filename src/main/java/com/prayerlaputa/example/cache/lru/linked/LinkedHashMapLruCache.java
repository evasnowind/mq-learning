package com.prayerlaputa.example.cache.lru.linked;

import com.prayerlaputa.example.cache.lru.LowSpeedStorage;
import com.prayerlaputa.example.cache.lru.LruCache;
import com.prayerlaputa.example.cache.lru.Storage;

import java.util.HashMap;
import java.util.Map;

/**
 * HashMap + 双向链表实现
 * 查找、添加、删除时间复杂度都为O(1)
 *
 * @author chenglong.yu
 * created on 2020/9/2
 */
public class LinkedHashMapLruCache<K, V> extends LruCache {

    class Node<V> {
        V val;
        K key;
        Node prev;
        Node next;

        public Node() {}

        public Node(K key, V val) {
            this.key = key;
            this.val = val;
        }
    }


    private Map<Object, Node> interHashMap;
    private Node head;
    private Node tail;

    public LinkedHashMapLruCache(int capacity, Storage lowSpeedStorage) {
        super(capacity, lowSpeedStorage);
        interHashMap = new HashMap<>(capacity);
        head = new Node();
        tail = new Node();
        head.next = tail;
        tail.prev = head;
    }


    @Override
    public Object get(Object key) {
        Node node;
        if ((node = interHashMap.get(key)) == null) {
            //1. 没有找到数据节点，需要先从低速存储获取该数据，进行put操作
            put(key, lowSpeedStorage.get(key));
            node = interHashMap.get(key);
        }

        //2. 拿到数据后，需要先将其移动到链表头部，表明该数据最近访问过
        moveToHead(node);
        return node.val;
    }

    private void put(Object key, Object val) {
        Node node = interHashMap.get(key);
        if (null == node) {
            //1. 内部hashmap中没有该数据，需要先将数据写入到map、加到链表头部，表明该数据最近访问过
            node = new Node();
            node.key = key;
            node.val = val;

            if (interHashMap.size() == capacity) {
                //此时内部hashmap已满，需要先删除链表尾节点
                Node tailDataNode = removeTailNode();
                interHashMap.remove(tailDataNode.key);
                tailDataNode = null;
            }
            //将新获取的数据加入到链表头部，并且存入内部hashmap中
            addToHead(node);
            interHashMap.put(key, node);
        } else {
            //2. 内部hashmap有该数据，需要更新该数据，同事将其移动到链表头部，表明该数据最近访问过
            node.val = val;
            moveToHead(node);
        }

    }

    private void addToHead(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.next = null;
        node.prev = null;
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    private Node removeTailNode() {
        Node node = tail.prev;
        if (node == head) {
            return head;
        }
        node.prev.next = tail;
        tail.prev = node.prev;
        node.next = null;
        node.prev = null;
        return node;
    }

    public static void main(String[] args) {
        Storage<Integer, String> lowSpeedStorage = new LowSpeedStorage();

        LinkedHashMapLruCache lruCache = new LinkedHashMapLruCache(4, lowSpeedStorage);
        System.out.println(lruCache.get(1));
        System.out.println(lruCache.get(2));
        System.out.println(lruCache.get(3));
        System.out.println(lruCache.get(4));
        System.out.println(lruCache.get(5));
        System.out.println(lruCache.get(1));
    }

}

