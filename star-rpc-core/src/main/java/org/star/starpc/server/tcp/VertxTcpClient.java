package org.star.starpc.server.tcp;

import io.vertx.core.Vertx;

public class VertxTcpClient {

    public void start() {
        Vertx vertx = Vertx.vertx();

        vertx.createNetClient().connect(8888, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Connected!");
                io.vertx.core.net.NetSocket socket = res.result();
                socket.write("hello rpc");
                socket.handler(buffer -> {
                    System.out.println("Net client receiving: " + buffer.toString("UTF-8"));
                });
            } else {
                System.out.println("Failed to connect: " + res.cause());
            }
        });
    }

    public static void main(String[] args) {
        VertxTcpClient client = new VertxTcpClient();
        client.start();
    }
}
