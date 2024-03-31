package org.star.starpc.fault.retry;

import lombok.extern.slf4j.Slf4j;
import org.star.starpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 无重试策略
 */
@Slf4j
public class NoRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
