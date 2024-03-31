package org.star.starpc.fault.tolerant;

import org.star.starpc.model.RpcResponse;

import java.util.Map;

public class FailOverTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // TODO: implement this method
        return null;
    }
}
