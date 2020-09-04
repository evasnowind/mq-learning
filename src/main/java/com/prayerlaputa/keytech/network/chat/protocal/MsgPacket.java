package com.prayerlaputa.keytech.network.chat.protocal;

import com.prayerlaputa.keytech.network.chat.command.Command;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenglong.yu
 * created on 2020/8/5
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MsgPacket extends Packet {

    /**
     * 序号
     */
    private Integer no;

    /**
     * 会话ID
     */
    private Integer session;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 内容
     */
    private String content;

    @Override
    public Byte getCommand() {
        return Command.MSG;
    }
}
