package org.star.starpc.fault.tolerant;

import org.star.starpc.fault.retry.NoRetryStrategy;
import org.star.starpc.fault.retry.RetryStrategy;
import org.star.starpc.spi.SpiLoader;

public class TolerantStrategyFactory {

    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailFastTolerantStrategy();

    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }
}
