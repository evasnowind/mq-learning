package com.prayerlaputa.mqdemo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.Properties;

/**
 * @author chenglong.yu
 * created on 2020/8/10
 */
public class KafkaConsumerDemo {


    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "group1");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("max.poll.records", 1000);
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList("test"));

        ConsumerRecords<String, String> msgList=consumer.poll(1000);
        int msgNo = 0;
        for (ConsumerRecord<String, String> record : msgList) {
            System.out.println(msgNo + "--------------" + record);
            if (msgNo % 1000 == 0) {
                break;
            }
            msgNo += 1;
        }
    }
}
