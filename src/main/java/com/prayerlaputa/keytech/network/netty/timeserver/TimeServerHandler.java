package com.prayerlaputa.keytech.network.netty.timeserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author chenglong.yu
 * created on 2020/8/19
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {


        /*
        1. 采用NIO方式写入，但不用flip操作。netty内部维持了两个指针：读索引（read index）、写索引（write index）

        2. ChannelHandlerContext的write()或是 writeAndFlush() 返回值是一个ChannelFuture，进行异步操作，比如关闭
         */
        final ChannelFuture channelFuture = ctx.writeAndFlush(new UnixTime());
        channelFuture.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                assert channelFuture == future;
                ctx.close();
            }
        });

        //也可以选用下面这种写法
        //channelFuture.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

}
