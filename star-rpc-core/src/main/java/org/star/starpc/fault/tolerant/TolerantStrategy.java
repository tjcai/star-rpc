package org.star.starpc.fault.tolerant;

import org.star.starpc.model.RpcResponse;

import java.util.Map;
import java.util.concurrent.Callable;

public interface TolerantStrategy {

    /**
     * 容错策略
     * @param context
     * @param e
     * @return RpcResponse
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
