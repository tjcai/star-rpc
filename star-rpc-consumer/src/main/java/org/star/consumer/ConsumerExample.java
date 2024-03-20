package org.star.consumer;

import org.star.common.model.User;
import org.star.common.service.UserService;
import org.star.starpc.RpcApplication;
import org.star.starpc.config.RpcConfig;
import org.star.starpc.proxy.ServiceProxyFactory;

public class ConsumerExample {
    public static void main(String[] args) {
        // 1. load config
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("star");
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
            System.out.println(userService.getNumber());
        } else {
            System.out.println("newUser is null");
        }
    }
}
