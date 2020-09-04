package com.prayerlaputa.keytech.network.chat.protocal;

import lombok.Data;

/**
 * @author chenglong.yu
 * created on 2020/8/5
 */
@Data
public abstract class Packet {

    /**
     * 协议版本
     */
    private Byte version = 1;

    /**
     * 指令
     */
    public abstract Byte getCommand();
}
