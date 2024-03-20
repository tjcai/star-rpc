package org.star.provider;

import org.star.common.service.UserService;
import org.star.starpc.RpcApplication;
import org.star.starpc.config.RegistryConfig;
import org.star.starpc.config.RpcConfig;
import org.star.starpc.model.ServiceMetaInfo;
import org.star.starpc.registry.LocalRegistry;
import org.star.starpc.registry.Registry;
import org.star.starpc.registry.RegistryFactory;
import org.star.starpc.server.HttpServer;
import org.star.starpc.server.VertxHttpServer;

public class ProviderExample {
    public static void main(String[] args) {
        RpcApplication.init();

        String serviceName = UserService.class.getName();
        // register to local
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // register to registry service
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        // get Instance of ETCD
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceAddr(rpcConfig.getServerHost() + ":" + rpcConfig.getServerPort());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // start server
        HttpServer server = new VertxHttpServer();
        server.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
