package com.prayerlaputa.mqdemo.rocketmq.schedule;

import com.prayerlaputa.mqdemo.util.ConfigUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author chenglong.yu
 * created on 2020/8/12
 */
public class ScheduledMessageProducerDemo {

    public static void main(String[] args) {
        DefaultMQProducer producer = new DefaultMQProducer("scheduledProducer");

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
                        "HelloWorld".getBytes(RemotingHelper.DEFAULT_CHARSET));
                /*
                官方文档又没说清楚……
                此处配置的延时发送配置，实际上是配置了一个延时级别，
                rocketmq broker中包含有messageDelayLevel配置，
                比如delayTimeLevel=3对应10s延迟，delayTimeLevel=4对应30s延迟。
                可以在broker修改这个值。
                参见 https://blog.csdn.net/u014380653/article/details/52883356

                默认值如下：
                1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h

                 */
                msg.setDelayTimeLevel(4);
                producer.send(msg, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        countDownLatch.countDown();
                        System.out.printf("%tT  %-10d ok  %s %n", new Date(), index, sendResult.getMsgId());
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
