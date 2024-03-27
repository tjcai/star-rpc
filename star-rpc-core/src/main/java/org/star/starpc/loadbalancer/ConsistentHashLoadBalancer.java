package org.star.starpc.loadbalancer;

import org.star.starpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoadBalancer {

    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    private final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }

        // 初次分配 hash （暂时用 object hashcode）
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i ++) {
                int currHash = getHash(serviceMetaInfo.getServiceAddr() + "&&" + i);
                virtualNodes.put(currHash, serviceMetaInfo);
            }
        }

        int hash = getHash(requestParams);

        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) {
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }

    private int getHash(Object key) {
        return key.hashCode();
    }
}
