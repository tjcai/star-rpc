package org.star.starpc.protocol;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum ProtocolSerializerTypeEnum {
    JDK(0, "jdk"),
    JSON(1, "json"),
    KRYO(2, "kryo"),
    HESSIAN(3, "hessian");

    private final int key;
    private final String value;


    ProtocolSerializerTypeEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    // 获取所有的枚举值
    public static List<String> getValues() {
        List<String> values = new ArrayList<>();
        for (ProtocolSerializerTypeEnum type : ProtocolSerializerTypeEnum.values()) {
            values.add(type.value);
        }
        return values;
    }

    public static ProtocolSerializerTypeEnum getEnumByKey(int key) {
        for (ProtocolSerializerTypeEnum type : ProtocolSerializerTypeEnum.values()) {
            if (type.key == key) {
                return type;
            }
        }
        return null;
    }

    public static ProtocolSerializerTypeEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (ProtocolSerializerTypeEnum type : ProtocolSerializerTypeEnum.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}
