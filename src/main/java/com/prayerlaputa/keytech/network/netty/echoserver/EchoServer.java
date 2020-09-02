package com.prayerlaputa.keytech.network.netty.echoserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author chenglong.yu
 * created on 2020/8/19
 */
public class EchoServer {
    private int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //The handler specified here will always be evaluated by a newly accepted Channel.
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    })
                    /*
                    继续初始化channel，完整的ChannelOption选项参见https://netty.io/4.1/api/io/netty/channel/ChannelOption.html
                    完整的ChannelConfig https://netty.io/4.1/api/io/netty/channel/ChannelConfig.html

                     */
                    .option(ChannelOption.SO_BACKLOG, 128)
                    /*
                    option() is for the NioServerSocketChannel that accepts incoming connections.
                    childOption() is for the Channels accepted by the parent ServerChannel, which is NioServerSocketChannel in this case.
                     */
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            System.out.println("开始监听端口" + port);
            // Bind and start to accept incoming connections.
            ChannelFuture channelFuture = bootstrap.bind(port).sync();

            System.out.println("结束监听");

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            channelFuture.channel().closeFuture().sync();
            System.out.println("closeFuture 操作结束");
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new EchoServer(port).run();
    }
}
