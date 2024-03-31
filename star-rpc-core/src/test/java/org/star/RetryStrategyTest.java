package org.star;

import org.junit.Test;
import org.star.starpc.fault.retry.NoRetryStrategy;
import org.star.starpc.fault.retry.RetryStrategy;
import org.star.starpc.model.RpcResponse;

public class RetryStrategyTest {

    RetryStrategy retryStrategy = new NoRetryStrategy();

    @Test
    public void doRetry() {
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("test retry");
                throw new RuntimeException("error");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
