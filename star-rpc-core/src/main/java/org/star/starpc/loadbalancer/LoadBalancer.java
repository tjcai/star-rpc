package org.star.starpc.loadbalancer;

import org.star.starpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

public interface LoadBalancer {

    /**
     * 选择一个服务提供者
     * @param requestParams             请求参数
     * @param serviceMetaInfoList       服务提供者列表
     * @return
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
