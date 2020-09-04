package com.prayerlaputa.mqdemo.rocketmq.simpleExample;

import com.prayerlaputa.mqdemo.util.ConfigUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * @author chenglong.yu
 * created on 2020/8/12
 */
public class RocketOneWayModeProducerDemo {

    public static void main(String[] args) {
        DefaultMQProducer producer = new DefaultMQProducer("oneWayModeProducer");

        String ipPort = ConfigUtil.getConfigFromFile(ConfigUtil.VPS_SERVER_ROCKET_MQ_IP_PORT);
        System.out.println("ipPort=" + ipPort);
        producer.setNamesrvAddr(ipPort);
        producer.setSendMsgTimeout(10000);

        try {
            producer.start();

            int messageCount = 100;
            for (int i = 0; i < messageCount; i++) {
                final int index = i;
                Message msg = new Message("TopicTest",
                        "TagA",
                        "HelloWorld".getBytes(RemotingHelper.DEFAULT_CHARSET));
                producer.sendOneway(msg);
            }

            Thread.sleep(10000);
            producer.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
