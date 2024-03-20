package org.star.starpc.config;

import lombok.Data;

@Data
public class RegistryConfig {

    // name of registry center
    private String registry = "etcd";

    private String address = "http://localhost:2380";

    private String username;

    private String password;

    // time out for 30 seconds
    private Long timeout = 30000L;
}
