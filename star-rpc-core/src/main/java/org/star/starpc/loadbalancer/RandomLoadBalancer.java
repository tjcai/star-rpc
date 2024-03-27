package org.star.starpc.loadbalancer;

import org.star.starpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer {

    private final Random random =  new Random();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        int size = serviceMetaInfoList.size();
        switch (size) {
            case 0:
                return null;
            case 1:
                return serviceMetaInfoList.get(0);
            default:
                return serviceMetaInfoList.get(random.nextInt(size));
        }
    }
}
