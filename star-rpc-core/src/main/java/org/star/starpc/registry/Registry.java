package org.star.starpc.registry;

import org.star.starpc.config.RegistryConfig;
import org.star.starpc.model.ServiceMetaInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Registry {
    void init (RegistryConfig registryConfig);

    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    void unRegister(ServiceMetaInfo serviceMetaInfo);

    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    void destroy();

    // heart beat check for liveness
    void heartBeat();

    // watch service for going offline
    void watch(String serviceNodeKey);
}
