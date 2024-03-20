package org.star.starpc.server;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.star.starpc.RpcApplication;
import org.star.starpc.model.RpcRequest;
import org.star.starpc.model.RpcResponse;
import org.star.starpc.registry.LocalRegistry;
import org.star.starpc.serializer.Serializer;
import org.star.starpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 处理请求
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {


    @Override
    public void handle(HttpServerRequest request) {
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        System.out.println("Received request: " + request.method() + " " + request.uri());

        // 异步处理 HTTP 请求
        request.bodyHandler(buffer -> {
            byte[] bytes = buffer.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 构造响应对象
            RpcResponse rpcResponse = new RpcResponse();
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpc request is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try {
                // 获取调用的服务实现类，反射机制
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("http server handler :: success");
            } catch (Exception e) {
                rpcResponse.setException(e);
                rpcResponse.setMessage("error");
            }

            doResponse(request, rpcResponse, serializer);
        });
    }

    /**
     * 响应请求
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response();
        try {
            byte[] bytes = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
