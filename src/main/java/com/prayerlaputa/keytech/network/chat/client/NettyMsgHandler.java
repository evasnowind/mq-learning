package com.prayerlaputa.keytech.network.chat.client;

import com.prayerlaputa.keytech.network.chat.codec.PacketCodec;
import com.prayerlaputa.keytech.network.chat.common.MsgConst;
import com.prayerlaputa.keytech.network.chat.common.MsgCounter;
import com.prayerlaputa.keytech.network.chat.protocal.MsgPacket;
import com.prayerlaputa.keytech.network.chat.protocal.MsgRepository;
import com.prayerlaputa.keytech.network.chat.protocal.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author chenglong.yu
 * created on 2020/8/4
 */
public class NettyMsgHandler extends ChannelInboundHandlerAdapter {

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
                sendMsg(ctx, MsgConst.MSG_SESSION_ONE);
                break;
            }
            case MsgConst.MSG_SESSION_TWO:
            case MsgConst.MSG_SESSION_THREE: {
                MsgCounter.count();
                printMsg(msgPacket);
                break;
            }
            default: {
                break;
            }
        }
    }


    private void sendMsg(ChannelHandlerContext ctx, Integer sessionId) {
        MsgPacket liMsgPacket = MsgRepository.getInstance().getLiMsgPacket(sessionId);
        ByteBuf byteBuf = PacketCodec.getInstance().encode(liMsgPacket);
        ctx.writeAndFlush(byteBuf);
    }

    private void printMsg(MsgPacket packet) {
        System.out.println("张大爷说：【" + packet.getSession() + ":" + packet.getContent() + "】");
    }
}
