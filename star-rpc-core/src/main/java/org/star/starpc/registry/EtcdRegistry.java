package org.star.starpc.registry;

import cn.hutool.json.JSONUtil;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import org.star.starpc.config.RegistryConfig;
import org.star.starpc.model.ServiceMetaInfo;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于 ETCD 的服务注册
 */
public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    private static final String ETCD_ROOT_PATH = "/rpc/";


    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(
                Duration.ofMillis(registryConfig.getTimeout())
        ).build();
        kvClient = client.getKVClient();
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
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
//        kvClient.delete(getServiceKey(serviceMetaInfo));
        kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8));
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValPairs = kvClient.get(
                        ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                        getOption)
                    .get()
                    .getKvs();
            return keyValPairs.stream()
                    .map(keyVal -> {
                        String val = keyVal.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(val, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get service list with key: " + serviceKey + " " + e);
        }
    }

    @Override
    public void destroy() {
        System.out.println("Registry Service going offline");
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    private ByteSequence getServiceKey(ServiceMetaInfo serviceMetaInfo) {
        return ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8);
    }
}
