package com.prayerlaputa.mqdemo.rocketmq.batch;

import org.apache.rocketmq.common.message.Message;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author chenglong.yu
 * created on 2020/9/15
 */
public class MsgListSplitter implements Iterator<List<Message>> {

    //1MB
    private final int SIZE_LIMIT = 1000 * 1000;
    private final List<Message> messages;
    private int curIndex;


    public MsgListSplitter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public boolean hasNext() {
        return curIndex < messages.size();
    }

    @Override
    public List<Message> next() {
        int nextIndex = curIndex;
        int totalSize = 0;
        for (; nextIndex < messages.size(); nextIndex++) {
            Message message = messages.get(nextIndex);
            //统计一个消息的二进制位数
            int tmpSize = message.getTopic().length() + message.getBody().length;
            Map<String, String> properties = message.getProperties();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                tmpSize += entry.getKey().length() + entry.getValue().length();
            }

            //for log overhead
            tmpSize = tmpSize + 20;
            if (tmpSize > SIZE_LIMIT) {
                //大于1MB，则拆分
                if (nextIndex - curIndex == 0) {
                    nextIndex ++;
                }
                break;
            }

            if (tmpSize + totalSize > SIZE_LIMIT) {
                break;
            } else {
                totalSize += tmpSize;
            }
        }
        List<Message> subList = messages.subList(curIndex, nextIndex);
        curIndex = nextIndex;
        return subList;
    }
}
