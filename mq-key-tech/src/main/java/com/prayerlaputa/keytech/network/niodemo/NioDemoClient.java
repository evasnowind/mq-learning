package com.prayerlaputa.keytech.network.niodemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author chenglong.yu
 * created on 2020/8/4
 */
public class NioDemoClient {
    public static void main(String[] args) {
        try (SocketChannel channel = SocketChannel.open();
             Selector selector = Selector.open();
        ) {
            channel.configureBlocking(false);
            if (!channel.connect(new InetSocketAddress("127.0.0.1", 8881))) {
                while (!channel.finishConnect()) {
                }

                System.out.println("连接到服务器");
            }

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.putInt(1);
            buffer.flip();
            channel.write(buffer);
            channel.register(selector, SelectionKey.OP_READ, buffer);
            for (; ; ) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    if (next.isReadable()) {
                        doRead(next, selector);
                    }
                    iterator.remove();
                }
                Thread.sleep(2000);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void doRead(SelectionKey key, Selector selector) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.clear();
        socketChannel.read(buffer);
        int msg = buffer.getInt(0);
        System.out.println("客户端收到服务器返回的信息" + socketChannel.getLocalAddress() + "  " + msg);
        buffer.putInt(0, msg + 1);
        buffer.flip();
        socketChannel.write(buffer);

    }

}
