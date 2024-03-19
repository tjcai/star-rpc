package org.star.starpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.star.starpc.RpcApplication;
import org.star.starpc.model.RpcRequest;
import org.star.starpc.model.RpcResponse;
import org.star.starpc.serializer.Serializer;
import org.star.starpc.serializer.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 服务代理（JDK 动态代理）
 */
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getConfig().getSerializer());
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            byte[] bytes = serializer.serialize(rpcRequest);
            // TODO: 注册中心解决硬编码问题
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bytes)
                    .execute()) {
                byte[] responseBytes = httpResponse.bodyBytes();
                RpcResponse rpcResponse = serializer.deserialize(responseBytes, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
