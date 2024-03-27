package org.star.starpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {

    // 协议头
    private Header header;

    // 协议体
    private T body;


    @Data
    public static class Header {
        // 魔数
        private byte magic;
        // 协议版本
        private byte version;
        // 序列化
        private byte serializer;
        // 消息类型 (request/response)
        private byte type;
        // 状态
        private byte status;
        // 请求 ID
        private long requestId;
        // 消息体长度
        private int bodyLength;
    }

}
