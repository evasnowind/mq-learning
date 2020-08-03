package com.prayerlaputa.keytech.network.serilazation;

/**
 * @author chenglong.yu
 * created on 2020/8/3
 */
public interface Serializer {

    /**
     * 序列化算法
     */
    byte getSerializerAlgorithm();

    /**
     * java对象转换成二进制
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换成 java 对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
