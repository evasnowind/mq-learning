package com.prayerlaputa.mqdemo.rocketmq.simpleExample;

import com.prayerlaputa.mqdemo.util.ConfigUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * @author chenglong.yu
 * created on 2020/8/11
 */
public class RocketSyncProducerDemo {


    public static void main(String[] args) throws Exception {

        DefaultMQProducer producer = new DefaultMQProducer("producerDemoGroup");

        String ipPort = ConfigUtil.getConfigFromFile(ConfigUtil.VPS_SERVER_ROCKET_MQ_IP_PORT);
        System.out.println("ipPort=" + ipPort);
        producer.setNamesrvAddr(ipPort);
        producer.setSendMsgTimeout(10000);
        producer.start();

        System.out.println("start send msg....");
        for (int i = 0; i < 100; i++) {
            Message msg = new Message("TopicTest",
                    "TagA",
                    ("Hello RocketMQ" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);
        }
        System.out.println("end send.");
        producer.shutdown();
    }


}
