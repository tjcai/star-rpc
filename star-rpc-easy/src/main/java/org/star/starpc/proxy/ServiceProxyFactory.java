package org.star.starpc.proxy;
import java.lang.reflect.Proxy;

/**
 * 服务代理工厂 -- 创建代理对象
 * Q：为什么需要代理
 */
public class ServiceProxyFactory {

    public static <T> T getProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }
}