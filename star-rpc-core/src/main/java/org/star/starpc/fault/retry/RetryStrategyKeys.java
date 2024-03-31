package org.star.starpc.fault.retry;

public interface RetryStrategyKeys {

    String NO = "no";

    String FIXED_INTERVAL = "fixedInterval";

    String EXPONENTIAL_BACKOFF = "exponentialBackoff";
}
