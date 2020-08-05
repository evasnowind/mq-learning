package com.prayerlaputa.keytech.network.niodemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author chenglong.yu
 * created on 2020/8/4
 */
public class NioDemoServer {

    public static void main(String[] args) {
        try {
            ServerSocketChannel server = ServerSocketChannel.open().bind(new InetSocketAddress(8881));
            server.configureBlocking(false);
            Selector selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
            for (; ; ) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    if (next.isAcceptable()) {
                        acceptHandle(next, selector);
                    }
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

    public static void acceptHandle(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverShannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverShannel.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
    }

    public static void doRead(SelectionKey key, Selector selector) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.clear();
        int read = socketChannel.read(buffer);
        int msg = buffer.getInt(0);
        System.out.println("服务器收到客户端" + socketChannel.getLocalAddress() + "  " + msg);
        buffer.rewind();
        buffer.putInt(msg + 1);
        buffer.flip();
        socketChannel.write(buffer);
        //buffer.clear();
    }
}
