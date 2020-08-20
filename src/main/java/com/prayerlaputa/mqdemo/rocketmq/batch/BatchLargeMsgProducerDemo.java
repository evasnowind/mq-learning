package com.prayerlaputa.mqdemo.rocketmq.batch;

import com.prayerlaputa.mqdemo.util.ConfigUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chenglong.yu
 * created on 2020/8/12
 */
public class BatchLargeMsgProducerDemo {

    public static void main(String[] args) {
        DefaultMQProducer producer = new DefaultMQProducer("batchProducer");

        String ipPort = ConfigUtil.getConfigFromFile(ConfigUtil.VPS_SERVER_ROCKET_MQ_IP_PORT);
        System.out.println("ipPort=" + ipPort);
        producer.setNamesrvAddr(ipPort);
        producer.setSendMsgTimeout(10000);

        try {
            producer.start();
            producer.setRetryTimesWhenSendAsyncFailed(3);
            int messageCount = 200;

            List<Message> messageList = new ArrayList<>();
            for (int i = 0; i < messageCount; i++) {
                messageList.add(new Message("BatchTopic",
                        "TagA",
                        "OrderId188",
                        ("HelloWorld" + i).getBytes(RemotingHelper.DEFAULT_CHARSET)));
            }

            MsgListSplitter splitter = new MsgListSplitter(messageList);
            while (splitter.hasNext()) {
                List<Message> listItem = splitter.next();
                producer.send(listItem);
                System.out.println("发送批量消息...");
            }

            producer.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
