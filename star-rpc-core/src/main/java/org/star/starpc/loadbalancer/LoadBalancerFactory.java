package org.star.starpc.loadbalancer;

import org.star.starpc.spi.SpiLoader;

public class LoadBalancerFactory {

    static {
        SpiLoader.load(LoadBalancer.class);
        System.out.println("+++ LoadBalancer loaded");
        System.out.println(SpiLoader.getInstance(LoadBalancer.class, "roundRobin"));
    }

    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    public static LoadBalancer getInstance(String key) {
        System.out.println("Load Balancer key is : " + key);
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }
}
