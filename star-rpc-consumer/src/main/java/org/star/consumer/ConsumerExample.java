package org.star.consumer;

import org.star.starpc.RpcApplication;
import org.star.starpc.config.RpcConfig;
import org.star.starpc.utils.ConfigUtils;

public class ConsumerExample {
    public static void main(String[] args) {
        // 1. load config
//        RpcConfig rpcConfig = RpcApplication.getConfig();
        System.out.println(ConfigUtils.loadConfig(RpcConfig.class, "rpc"));
    }
}
