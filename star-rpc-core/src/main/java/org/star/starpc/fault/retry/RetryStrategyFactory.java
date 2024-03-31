package org.star.starpc.fault.retry;

import org.star.starpc.spi.SpiLoader;

public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    public static RetryStrategy getInstance(String retryStrategyKey) {
        return SpiLoader.getInstance(RetryStrategy.class, retryStrategyKey);
    }
}
