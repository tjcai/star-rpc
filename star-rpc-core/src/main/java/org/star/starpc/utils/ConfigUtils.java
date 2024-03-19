package org.star.starpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

public class ConfigUtils {

    /**
     * 加载配置文件
     * @param clazz
     * @param prefix
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix) {
        return loadConfig(clazz, prefix, "");
    }

    /**
     * 加载配置文件；指定环境
     * @param clazz
     * @param prefix
     * @param env
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix, String env) {
        StringBuilder configStringBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(env)) {
            configStringBuilder.append("-").append(env);
        }
        configStringBuilder.append(".properties");
        Props props = new Props(configStringBuilder.toString());
        props.autoLoad(true);
        return props.toBean(clazz, prefix);
    }
}
