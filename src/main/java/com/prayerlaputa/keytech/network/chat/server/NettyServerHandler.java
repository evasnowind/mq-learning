package com.prayerlaputa.keytech.network.chat.server;

import com.prayerlaputa.keytech.network.chat.codec.PacketCodec;
import com.prayerlaputa.keytech.network.chat.common.MsgConst;
import com.prayerlaputa.keytech.network.chat.common.MsgCounter;
import com.prayerlaputa.keytech.network.chat.protocal.MsgPacket;
import com.prayerlaputa.keytech.network.chat.protocal.MsgRepository;
import com.prayerlaputa.keytech.network.chat.protocal.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;
import java.util.Optional;

/**
 * @author chenglong.yu
 * created on 2020/8/4
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx)
            throws Exception {
        System.out.println("channelRegistered: channel注册到NioEventLoop");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx)
            throws Exception {
        System.out.println("channelUnregistered: channel取消和NioEventLoop的绑定");
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)
            throws Exception {
        System.out.println("channelInactive: channel被关闭");
        super.channelInactive(ctx);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
            throws Exception {
        System.out.println("channelReadComplete: channel读数据完成");
        super.channelReadComplete(ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx)
            throws Exception {
        System.out.println("handlerAdded: handler被添加到channel的pipeline");
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)
            throws Exception {
        System.out.println("handlerRemoved: handler从channel的pipeline中移除");
        super.handlerRemoved(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(new Date() + " CLIENT: " + getRemoteAddress(ctx) + " 接入连接");
        // 往channel map中添加channel信息
        NettyMsgServer.putChannel(getHostString(ctx), ctx.channel());
    }

    private static String getRemoteAddress(ChannelHandlerContext ctx) {
        return Optional
                .ofNullable(
                        ctx.channel()
                                .remoteAddress()
                                .toString())
                .orElse("")
                .replace("/", "");
    }

    private static String getHostString(ChannelHandlerContext ctx) {
        String address = getRemoteAddress(ctx);
        return address.substring(0, address.indexOf(":"));
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf requestByteBuf = (ByteBuf) msg;

        // 解码
        Packet packet = PacketCodec.getInstance().decode(requestByteBuf);

        // 判断是否是消息请求数据包
        if (!(packet instanceof MsgPacket)) {
            return;
        }

        MsgPacket msgPacket = (MsgPacket) packet;
        Integer session = msgPacket.getSession();
        switch (session) {
            case MsgConst.MSG_SESSION_ONE: {
                MsgCounter.count();
                printMsg(msgPacket);
                break;
            }
            case MsgConst.MSG_SESSION_TWO: {
                MsgCounter.count();
                printMsg(msgPacket);
                sendMsg(ctx, MsgConst.MSG_SESSION_TWO);
                break;
            }
            case MsgConst.MSG_SESSION_THREE: {
                MsgCounter.count();
                printMsg(msgPacket);
                sendMsg(ctx, MsgConst.MSG_SESSION_THREE);
                break;
            }
            default: {
                break;
            }
        }

    }

    private void sendMsg(ChannelHandlerContext ctx, Integer sessionId) {
        MsgPacket liMsgPacket = MsgRepository.getInstance().getZhangMsgPacket(sessionId);
        ByteBuf byteBuf = PacketCodec.getInstance().encode(liMsgPacket);
        ctx.writeAndFlush(byteBuf);
    }

    private void printMsg(MsgPacket packet) {
        System.out.println("李大爷说：【" + packet.getSession() + ":" + packet.getContent() + "】");
    }
}
