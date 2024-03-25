package org.star.starpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import org.star.starpc.config.RegistryConfig;
import org.star.starpc.model.ServiceMetaInfo;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于 ETCD 的服务注册
 */
public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    // local registered node list -> maintain liveness
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    // local cache for registry service & 注册中心服务缓存
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    // key set for watch & 监听的 key 集合
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    private static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(
                Duration.ofMillis(registryConfig.getTimeout())
        ).build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        Lease leaseClient = client.getLeaseClient();
        // create 30 seconds lease
        long leaseId = leaseClient.grant(30).get().getID();
        // set k-v pairs
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);

//        ByteSequence key = getServiceKey(serviceMetaInfo);
        ByteSequence val = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, val, putOption).get();

        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
//        kvClient.delete(getServiceKey(serviceMetaInfo));
        kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8));
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // read from local cache
        List<ServiceMetaInfo> cachedServiceList = registryServiceCache.readCache();
        if (cachedServiceList != null) {
            return cachedServiceList;
        }

        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValPairs = kvClient.get(
                        ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                        getOption)
                    .get()
                    .getKvs();
            List<ServiceMetaInfo> serviceMetaInfoList = keyValPairs.stream()
                    .map(keyVal -> {
                        String key = keyVal.getKey().toString(StandardCharsets.UTF_8);
                        // watch the key & 监听该 key
                        watch(key);
                        String val = keyVal.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(val, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get service list with key: " + serviceKey + " " + e);
        }
    }

    @Override
    public void destroy() {
        for (String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key + " failed to go offline");
            }
        }
        System.out.println("Registry Service going offline");
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        CronUtil.schedule("*/20 * * * * *", new Task() {
            @Override
            public void execute() {
                for (String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> keyVals = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8)).get().getKvs();
                        if (CollUtil.isEmpty(keyVals)) {
                            continue;
                        }
                        KeyValue keyVal = keyVals.get(0);
                        String val = keyVal.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(val, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "fail to maintain liveness", e);
                    }
                }
            }
        });

        // support second level scheduled task
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        boolean watching = watchingKeySet.add(serviceNodeKey);
        if (watching) {
            watchClient.watch(
                    ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8),
                    watchResponse -> {
                        List<WatchEvent> events = watchResponse.getEvents();
                        for (WatchEvent event : events) {
                            switch (event.getEventType()) {
                                case PUT:
                                case DELETE:
                                    registryServiceCache.clearCache();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
            );

        }
    }

    private ByteSequence getServiceKey(ServiceMetaInfo serviceMetaInfo) {
        return ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8);
    }
}
