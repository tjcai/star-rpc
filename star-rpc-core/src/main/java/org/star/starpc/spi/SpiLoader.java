package org.star.starpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.star.starpc.serializer.Serializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpiLoader {

    /**
     * 存储加载的类：名字 - Serializer 方法名 - 实现类
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 存储类实例对象
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统提供的 SPI 文件路径
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc-config/system/";

    /**
     * 用户自定义的 SPI 文件路径
     */
    private static final String RPC_USER_SPI_DIR = "META-INF/rpc-config/user/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[] {
            RPC_SYSTEM_SPI_DIR,
            RPC_USER_SPI_DIR
    };

    /**
     * 加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    public static void loadAll() {
        log.info("Load all spi classes");
        for (Class<?> clazz : LOAD_CLASS_LIST) {
            load(clazz);
        }
    }

    /**
     * 加载某个类型
     * @param loadClass
     * @return
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("load spi with name: {}", loadClass.getName());
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for(String dir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(dir + loadClass.getName());
            // 针对每一个 URL 进行加载
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] strSplit = line.split("=");
                        if (strSplit.length > 1) {
                            String key = strSplit[0];
                            String className = strSplit[1];
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }
                } catch (Exception e) {
                    log.error("load spi class error", e);
                }
            }
        }

        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }

    /**
     * 获取接口实例
     * @param clazz
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<T> clazz, String key) {
        String className = clazz.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(className);

        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader cannot load class: %s", className));
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader cannot identify key (%s) in class (%s)", key, className));
        }
        Class<?> implClass = keyClassMap.get(key);
        String implClassName = implClass.getName();
        // instanceCache 缓存获得实现类的实例
        if (!instanceCache.containsKey(implClassName)) {
            // 1. get instance
            try {
                instanceCache.put(implClassName, implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(String.format("Failed to create instance of class: %s", implClassName));
            }
        }
        return (T) instanceCache.get(implClassName);

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        loadAll();
        System.out.println(loaderMap);
        Serializer serializer = getInstance(Serializer.class, "json");
        System.out.println(serializer);
    }
}
