package org.star.starpc.bootstrap;

import org.star.starpc.RpcApplication;
import org.star.starpc.config.RegistryConfig;
import org.star.starpc.config.RpcConfig;
import org.star.starpc.model.ServiceMetaInfo;
import org.star.starpc.model.ServiceRegisterInfo;
import org.star.starpc.registry.LocalRegistry;
import org.star.starpc.registry.Registry;
import org.star.starpc.registry.RegistryFactory;
import org.star.starpc.server.HttpServer;
import org.star.starpc.server.VertxHttpServer;

import java.util.List;

public class ProviderBootstrap {

    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfos) {
        RpcApplication.init();
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfos) {
            String serviceName = serviceRegisterInfo.getServiceName();
            LocalRegistry.register(serviceName, serviceRegisterInfo.getServiceImpl());

            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " fail to register service", e);
            }

            // start server
            HttpServer server = new VertxHttpServer();
            server.doStart(RpcApplication.getRpcConfig().getServerPort());
        }
    }
}
