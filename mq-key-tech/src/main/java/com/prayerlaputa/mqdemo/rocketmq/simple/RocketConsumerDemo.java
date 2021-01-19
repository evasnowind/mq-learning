package com.prayerlaputa.mqdemo.rocketmq.simple;

import com.prayerlaputa.mqdemo.util.ConfigUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
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
public class RocketConsumerDemo {


    static int consumeCnt = 0;

    public static void main(String[] args) {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("rocketmq_consumer_group");
        String ipPort = ConfigUtil.getStringValue(ConfigUtil.VPS_SERVER_ROCKET_MQ_IP_PORT);
        System.out.println("ipPort=" + ipPort);
        consumer.setNamesrvAddr(ipPort);


        try {
            consumer.subscribe("TopicTest", "*");

            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    /*
                    2020 09-14
                    此处的例子，虽然传入的msgs是List，但实际上传入的消息还是一条。
                    想发多条的话可能需要使用批量发送的接口？有待进一步学习。
                    内部是否和kafka一样，采用批量发送的方式，有待进一步探究。

                     */
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
