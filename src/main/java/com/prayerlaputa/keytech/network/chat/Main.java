package com.prayerlaputa.keytech.network.chat;

import com.prayerlaputa.keytech.network.chat.client.NettyMsgClient;
import com.prayerlaputa.keytech.network.chat.codec.PacketCodec;
import com.prayerlaputa.keytech.network.chat.common.MsgConst;
import com.prayerlaputa.keytech.network.chat.protocal.MsgPacket;
import com.prayerlaputa.keytech.network.chat.protocal.MsgRepository;
import com.prayerlaputa.keytech.network.chat.protocal.Packet;
import com.prayerlaputa.keytech.network.chat.server.NettyMsgServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;


/**
 * @author chenglong.yu
 * created on 2020/8/4
 */
public class Main {



    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = NettyMsgServer.createBootstrap();
        ChannelFuture serverFuture = NettyMsgServer.bind(serverBootstrap, MsgConst.PORT);
        Bootstrap clientBootstrap = NettyMsgClient.createBootstrap();
        ChannelFuture clientFuture = NettyMsgClient.connect(clientBootstrap, MsgConst.HOST, MsgConst.PORT, MsgConst.MAX_RETRY);
        try {
            serverFuture.await();
            clientFuture.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 10; i++) {
            MsgPacket one = MsgRepository.getInstance().getZhangMsgPacket(MsgConst.MSG_SESSION_ONE);
            MsgPacket two = MsgRepository.getInstance().getLiMsgPacket(MsgConst.MSG_SESSION_TWO);
            MsgPacket three = MsgRepository.getInstance().getLiMsgPacket(MsgConst.MSG_SESSION_THREE);
            sendMsg(NettyMsgServer.getChannel(MsgConst.HOST), one);
            sendMsg(clientFuture.channel(), two);
            sendMsg(clientFuture.channel(), three);
        }
    }

    private static void sendMsg(Channel channel, Packet packet) {
        ByteBuf byteBuf = PacketCodec.getInstance().encode(packet);
        channel.writeAndFlush(byteBuf);
    }
}
