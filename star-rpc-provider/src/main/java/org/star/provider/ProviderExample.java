package org.star.provider;

import org.star.common.service.UserService;
import org.star.starpc.RpcApplication;
import org.star.starpc.config.RpcConfig;
import org.star.starpc.registry.LocalRegistry;
import org.star.starpc.server.HttpServer;
import org.star.starpc.server.VertxHttpServer;

public class ProviderExample {
    public static void main(String[] args) {
        // 1. init rpc
        RpcConfig rpcConfig = RpcApplication.getConfig();
        // 2. register service
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        // 3. create server
        HttpServer server = new VertxHttpServer();
        // 4. start server with config port
        server.doStart(RpcApplication.getConfig().getServerPort());
    }
}
