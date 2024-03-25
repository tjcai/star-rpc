package org.star.starpc.registry;

import org.star.starpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * 服务注册缓存
 * local cache for registry service
 */
public class RegistryServiceCache {

    List<ServiceMetaInfo> serviceCache;

    void writeCache(List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache = newServiceCache;
    }

    List<ServiceMetaInfo> readCache() {
        return this.serviceCache;
    }

    void clearCache() {
        this.serviceCache = null;
    }
}
