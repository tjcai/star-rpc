package org.star.starpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.star.starpc.RpcApplication;
import org.star.starpc.config.RpcConfig;
import org.star.starpc.constant.RpcConstant;
import org.star.starpc.loadbalancer.LoadBalancer;
import org.star.starpc.loadbalancer.LoadBalancerFactory;
import org.star.starpc.model.RpcRequest;
import org.star.starpc.model.RpcResponse;
import org.star.starpc.model.ServiceMetaInfo;
import org.star.starpc.registry.Registry;
import org.star.starpc.registry.RegistryFactory;
import org.star.starpc.serializer.Serializer;
import org.star.starpc.serializer.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务代理（JDK 动态代理）
 */
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = method.getDeclaringClass().getName();
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            byte[] bytes = serializer.serialize(rpcRequest);

            // get provider address from service registry
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            // set serviceMetaInfo values -> get serviceKey
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("no service provided");
            }
            // load balancer
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo chosenServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

            try (HttpResponse httpResponse = HttpRequest.post(chosenServiceMetaInfo.getServiceAddr())
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
