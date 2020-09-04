package com.prayerlaputa.keytech.network.chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author chenglong.yu
 * created on 2020/8/4
 */

public class NettyMsgClient {


    public static final int MAX_RETRY = 5;
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 18000;


    public static Bootstrap createBootstrap() {
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
                //设置reactor线程
        bootstrap.group(workGroup)
                //设置channel类型
                .channel(NioSocketChannel.class)
                //连接超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //通道选项
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                //装配流水线
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7, 4));
                        socketChannel.pipeline().addLast(new NettyMsgHandler());
                    }
                });
        return bootstrap;
    }

    public static ChannelFuture connect(Bootstrap bootstrap, String host, int port, int retryTime) {
        return bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println(new Date() + ": 连接成功！");
            } else if (retryTime == 0) {
                System.err.println(new Date() + "重试次数已达到" + retryTime + ", 放弃连接！");
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retryTime) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                System.err.println(new Date() + ": 连接失败，第 " + order + " 次重连……");
                bootstrap.group()
                        .schedule(() -> connect(bootstrap, host, port, retryTime - 1), delay, TimeUnit.SECONDS);
            }
        });
    }
}
