package org.star.starpc.registry;

import org.star.starpc.config.RegistryConfig;
import org.star.starpc.model.ServiceMetaInfo;

import java.util.List;

public interface Registry {
    void init (RegistryConfig registryConfig);

    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    void unRegister(ServiceMetaInfo serviceMetaInfo);

    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    void destroy();
}
