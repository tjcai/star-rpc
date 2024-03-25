package org.star.starpc;

import lombok.extern.slf4j.Slf4j;
import org.star.starpc.config.RegistryConfig;
import org.star.starpc.config.RpcConfig;
import org.star.starpc.constant.RpcConstant;
import org.star.starpc.registry.Registry;
import org.star.starpc.registry.RegistryFactory;
import org.star.starpc.utils.ConfigUtils;

@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, rpcConfig: [{}]", rpcConfig.toString());

        // init registry
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, registryConfig: [{}]", registryConfig);

        // JVM hook for delete services
        Runtime.getRuntime().addShutdownHook(
                new Thread(registry::destroy)
        );
    }

    public static void init() {
        RpcConfig newRpcConfig;

        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            log.error("rpc init error", e);
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }

}
