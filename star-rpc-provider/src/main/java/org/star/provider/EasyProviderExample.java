package org.star.provider;

import org.star.common.service.UserService;
import org.star.starpc.registry.LocalRegistry;
import org.star.starpc.server.HttpServer;
import org.star.starpc.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        // 服务端启动
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
        System.out.println("Main provider func");;
    }
}
