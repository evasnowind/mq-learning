package com.prayerlaputa.mqdemo.rocketmq.filter;

import com.prayerlaputa.mqdemo.util.ConfigUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author chenglong.yu
 * created on 2020/8/12
 */
public class FilterProducerDemo {

    public static void main(String[] args) {
        DefaultMQProducer producer = new DefaultMQProducer("filterProducer");

        String ipPort = ConfigUtil.getConfigFromFile(ConfigUtil.VPS_SERVER_ROCKET_MQ_IP_PORT);
        System.out.println("ipPort=" + ipPort);
        producer.setNamesrvAddr(ipPort);
        producer.setSendMsgTimeout(10000);

        try {
            producer.start();
            producer.setRetryTimesWhenSendAsyncFailed(3);
            int messageCount = 100;
            final CountDownLatch countDownLatch = new CountDownLatch(messageCount);
            for (int i = 0; i < messageCount; i++) {
                final int index = i;
                Message msg = new Message("TopicTest",
                        "TagA",
                        "OrderId188",
                        ("HelloWorld" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                msg.putUserProperty("a", String.valueOf(i));
                producer.send(msg, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        countDownLatch.countDown();
                        System.out.printf("%-10d ok %s %n", index, sendResult.getMsgId());
                    }

                    @Override
                    public void onException(Throwable e) {
                        countDownLatch.countDown();
                        System.out.printf("%-10d exception %s %n", index, e);
                    }
                });
            }
            countDownLatch.await(10, TimeUnit.SECONDS);

            producer.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
