package com.prayerlaputa.mqdemo.rocketmq.transaction;

import com.prayerlaputa.mqdemo.util.ConfigUtil;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import javax.xml.transform.Transformer;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author chenglong.yu
 * created on 2020/9/14
 */
public class TransactionProducer {

    public static final String TRANSACTION_PRODUCER_GROUP = "transaction_producer";

    public static void main(String[] args) throws MQClientException, InterruptedException {
        TransactionMQProducer producer = new TransactionMQProducer(TRANSACTION_PRODUCER_GROUP);

        String ipPort = ConfigUtil.getConfigFromFile(ConfigUtil.VPS_SERVER_ROCKET_MQ_IP_PORT);
        System.out.println("ipPort=" + ipPort);
        producer.setNamesrvAddr(ipPort);

        ExecutorService executorService = new ThreadPoolExecutor(2,
                5,
                100,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("client-transaction-msg-check-thread");
                        return thread;
                    }
                });

        producer.setExecutorService(executorService);

        TransactionListener transactionListener = new MyTransactionListenerImpl();
        producer.setTransactionListener(transactionListener);
        producer.start();

        String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
        for (int i = 0; i < 10; i++) {
            Message msg = null;
            try {
                msg = new Message("TopicTest1234",
                        tags[i % tags.length],
                        "Keys-" + i,
                        ("Value-" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));

                SendResult sendResult = producer.sendMessageInTransaction(msg, null);
                System.out.printf("%s%n", sendResult);

                TimeUnit.MILLISECONDS.sleep(10);
            } catch (UnsupportedEncodingException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        TimeUnit.SECONDS.sleep(10000);
    }
}
