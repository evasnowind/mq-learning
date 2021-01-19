package com.prayerlaputa.mqdemo.rocketmq.order;

import com.prayerlaputa.mqdemo.util.ConfigUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.List;

/**
 * @author chenglong.yu
 * created on 2020/8/12
 */
public class OrderedProducer {


    public static void main(String[] args) throws Exception {
        //Instantiate with a producer group name.
        /*
        官网提供示例有点小问题，没写ip 端口怎么去发消息……
        再次吐槽，
         */
        DefaultMQProducer producer = new DefaultMQProducer("example_group_name");

        String ipPort = ConfigUtil.getConfigFromFile(ConfigUtil.VPS_SERVER_ROCKET_MQ_IP_PORT);
        System.out.println("ipPort=" + ipPort);
        producer.setNamesrvAddr(ipPort);
        producer.setSendMsgTimeout(10000);

        //Launch the instance.
        producer.start();
        String[] tags = new String[] {"TagA", "TagB", "TagC", "TagD", "TagE"};
        for (int i = 0; i < 100; i++) {
            int orderId = i % 10;
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("TopicTest", tags[i % tags.length], "KEY" + i,
                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    /*
                    此处自定义了一个MessageQueueSelector，用于决定在多个queue的情况，如何选择queue。
                    RocketMQ默认提供的是
                    - 哈希选择策略 SelectMessageQueueByHash
                    - 随机选择策略 SelectMessageQueueByRandom
                    - 同机房选择策略 electMessageQueueByMachineRoom

                    而此处实现的实际是轮转 RR的策略
                     */
                    Integer id = (Integer) arg;
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
            }, orderId);

            System.out.printf("%s%n", sendResult);
        }
        //server shutdown
        producer.shutdown();
    }
}
