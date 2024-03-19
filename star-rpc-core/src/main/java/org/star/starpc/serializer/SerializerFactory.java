package org.star.starpc.serializer;

import org.star.starpc.spi.SpiLoader;

/**
 * 序列化工厂，单例模式
 */
public class SerializerFactory {

    static {
        SpiLoader.load(Serializer.class);
        System.out.println("+++ SerializerFactory loaded");
        System.out.println(SpiLoader.getInstance(Serializer.class, "json"));
    }

    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    public static Serializer getInstance(String key) {
        System.out.println("key is : " + key);
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
