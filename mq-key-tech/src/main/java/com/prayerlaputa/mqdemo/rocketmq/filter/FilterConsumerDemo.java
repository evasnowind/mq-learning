package com.prayerlaputa.mqdemo.rocketmq.filter;

import com.prayerlaputa.mqdemo.util.ConfigUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author chenglong.yu
 * created on 2020/8/11
 */
public class FilterConsumerDemo {


    static int consumeCnt = 0;

    public static void main(String[] args) {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("filterConsumer");
        String ipPort = ConfigUtil.getStringValue(ConfigUtil.VPS_SERVER_ROCKET_MQ_IP_PORT);
        System.out.println("ipPort=" + ipPort);
        consumer.setNamesrvAddr(ipPort);


        try {
            consumer.subscribe("TopicTest", MessageSelector.bySql("a between 0 and 10"));

            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                    System.out.println();
                    System.out.println("consume cnt=" + consumeCnt  + " -----------------------------------------");
                    consumeCnt ++;
//                    System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);

                    for (int i = 0; i < msgs.size(); i++) {
                        System.out.printf("%s Receive New Messages: i=%d body=%s %n",
                                Thread.currentThread().getName(), i, new String(msgs.get(i).getBody(), StandardCharsets.UTF_8));
                    }
                    System.out.println("-----------------------------------------");

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

            consumer.start();

        } catch (MQClientException e) {
            e.printStackTrace();
        }

        System.out.printf("Consumer Started.%n");
    }
}
