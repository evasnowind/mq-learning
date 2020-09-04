package com.prayerlaputa.keytech.network.chat.codec;

import com.prayerlaputa.keytech.network.chat.command.Command;
import com.prayerlaputa.keytech.network.chat.protocal.MsgPacket;
import com.prayerlaputa.keytech.network.chat.protocal.Packet;
import com.prayerlaputa.keytech.network.chat.serializer.Serializer;
import com.prayerlaputa.keytech.network.chat.serializer.SerializerAlgorithm;
import com.prayerlaputa.keytech.network.chat.serializer.impl.ProtoStuffSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenglong.yu
 * created on 2020/8/5
 *
 * 通信协议
 * 魔数0x12345678(4字节)|版本号(1字节)|序列化算法(1字节)|指令(1字节)|数据长度(4字节)|数据(N字节)
 */
public class PacketCodec {

    private static PacketCodec instance = new PacketCodec(new ProtoStuffSerializer());


    private static final int MAGIC_NUMBER = 0x12345678;

    private Serializer serializer;

    private static final Map<Byte, Serializer> serializerMap;
    private static final Map<Byte, Class<? extends Packet>> packetTypeMap;

    static {
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(Command.MSG, MsgPacket.class);

        serializerMap = new HashMap<>();
        Serializer protoStuffSerializer = new ProtoStuffSerializer();
        serializerMap.put(SerializerAlgorithm.PROTO_STUFF, protoStuffSerializer);

    }

    public static PacketCodec getInstance() {
        return instance;
    }

    public PacketCodec(Serializer serializer) {
        this.serializer = serializer;
    }

    public ByteBuf encode(Packet packet) {
        // 1. 创建 ByteBuf 对象
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        // 2. 序列化 Java 对象
        byte[] bytes = serializer.serialize(packet);

        // 3. 实际编码过程
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(serializer.getSerializerAlgorithm());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        return byteBuf;
    }


    public Packet decode(ByteBuf byteBuf) {
        // 跳过 magic number
        byteBuf.skipBytes(4);

        // 跳过版本号
        byteBuf.skipBytes(1);

        // 序列化算法标识
        byte serializeAlgorithm = byteBuf.readByte();

        // 指令
        byte command = byteBuf.readByte();

        // 数据包长度
        int length = byteBuf.readInt();

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        Class<? extends Packet> requestType = getRequestType(command);
        Serializer serializer = getSerializer(serializeAlgorithm);

        if (requestType != null && serializer != null) {
            return serializer.deserialize(requestType, bytes);
        }

        return null;
    }

    private Serializer getSerializer(byte serializeAlgorithm) {
        return serializerMap.get(serializeAlgorithm);
    }

    private Class<? extends Packet> getRequestType(byte command) {
        return packetTypeMap.get(command);
    }
}
