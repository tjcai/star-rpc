package org.star.starpc.protocol;

import lombok.Getter;

@Getter
public enum ProtocolStatusEnum {

    OK("ok", 20),
    BAD_REQUEST("badRequest", 40),
    BAD_RESPONSE("badResponse", 50);

    private final String text;

    private final int value;

    ProtocolStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public static ProtocolStatusEnum getEnumByVal(int value) {
        for (ProtocolStatusEnum status : ProtocolStatusEnum.values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
