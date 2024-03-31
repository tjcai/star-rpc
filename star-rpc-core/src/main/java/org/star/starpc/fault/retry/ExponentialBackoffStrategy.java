package org.star.starpc.fault.retry;

import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;
import org.star.starpc.model.RpcResponse;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 指数退避重试策略
 */
@Slf4j
public class ExponentialBackoffStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.exponentialWait(100, 5, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("retrying... attempt: {}", attempt.getAttemptNumber());
                    }
                })
                .build();

        return retryer.call(callable);
    }
}
