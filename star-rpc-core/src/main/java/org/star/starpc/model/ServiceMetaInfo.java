package org.star.starpc.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.star.starpc.constant.RpcConstant;

/**
 * 注册信息
 */
@Data
public class ServiceMetaInfo {

    private String serviceName;

    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;

    private String serviceAddr;

    private String serviceHost;

    private Integer servicePort;

    private String serviceGroup = "default";

    public String getServiceKey() {
        return String.format(
                "%s:%s",
                serviceName,
                serviceVersion
        );
    }

    public String getServiceNodeKey() {
        if (serviceAddr != null) {
            return String.format("%s/%s", getServiceKey(), serviceAddr);
        }
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }

    public String getServiceAddr() {
        if (!StrUtil.contains(serviceHost, "http")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }
}
