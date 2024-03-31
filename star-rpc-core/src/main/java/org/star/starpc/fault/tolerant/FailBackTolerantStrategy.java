package org.star.starpc.fault.tolerant;

import lombok.extern.slf4j.Slf4j;
import org.star.starpc.model.RpcResponse;

import java.util.Map;

@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // TODO: implement this method
        return null;
    }
}
