package com.prayerlaputa.mqdemo.kafka;

import org.apache.kafka.common.errors.TimeoutException;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author chenglong.yu
 * created on 2020/9/11
 */
public class KafkaConsumerProducerDemo {

    public static void main(String[] args) throws InterruptedException {
        boolean isAsync = args.length == 0 || !args[0].trim().equalsIgnoreCase("sync");

        String topic = "testTopic";

        CountDownLatch latch = new CountDownLatch(2);

        int numRecords = 1000;
        KafkaProducerDemo producerThread = new KafkaProducerDemo(topic, isAsync, null, false, numRecords, -1, latch);
        producerThread.start();
        System.out.println("producer start...");

        KafkaConsumerDemo consumerThread = new KafkaConsumerDemo(topic, "DemoConsumer", Optional.empty(), false, numRecords, latch);
        consumerThread.start();
        System.out.println("consumer start...");

        if (!latch.await(5, TimeUnit.MINUTES)) {
            throw new TimeoutException("Timeout after 5 minutes waiting for demo producer and consumer to finish");
        }

        System.out.println("All finished!");
    }

}
