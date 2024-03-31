package org.star.starpc.config;

import lombok.Data;
import org.star.starpc.fault.retry.RetryStrategyKeys;
import org.star.starpc.fault.tolerant.TolerantStrategyKeys;
import org.star.starpc.loadbalancer.LoadBalancer;
import org.star.starpc.loadbalancer.LoadBalancerKeys;
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

    // 负载均衡策略
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    // 重试策略
    private String retryStrategy = RetryStrategyKeys.NO;

    // 容错策略
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;
}

