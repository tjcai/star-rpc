package org.star.starpc.config;

import lombok.Data;
import org.star.starpc.serializer.SerializerNames;

@Data
public class RpcConfig {
    private String name = "star-rpc";

    private String version = "1.0.0-SNAPSHOT";

    private Integer serverPort = 8080;

    private String serverHost = "localhost";

    // 模拟调用（用于测试，不会真正调用远程服务）
    private boolean mock = false;

    // 序列化方式
    private String serializer = SerializerNames.JDK;

    // 注册中心配置
    private RegistryConfig registryConfig = new RegistryConfig();
}

