package com.prayerlaputa.keytech.network.chat.protocal.packet;

import static com.prayerlaputa.keytech.network.chat.protocal.commond.Command.MSG;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author switch
 * @since 2019/10/12
 */
@Data
@NoArgsConstructor
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
        return MSG;
    }
}
