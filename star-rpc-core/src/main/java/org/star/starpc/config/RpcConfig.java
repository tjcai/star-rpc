package org.star.starpc.config;

import lombok.Data;

@Data
public class RpcConfig {
    private String name = "star-rpc";

    private String version = "1.0.0-SNAPSHOT";

    private Integer serverPort = 8080;

    private String serverHost = "localhost";
}
