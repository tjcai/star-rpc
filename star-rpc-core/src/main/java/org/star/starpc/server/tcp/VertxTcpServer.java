package org.star.starpc.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import org.star.starpc.server.HttpServer;

public class VertxTcpServer implements HttpServer {
    private byte[] handleRequest(byte[] request) {
        return "hello rpc".getBytes();
    }

    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        NetServer server = vertx.createNetServer();

        server.connectHandler(netSocket -> {
            netSocket.handler(buffer -> {
                byte[] bytes = buffer.getBytes();
                byte[] response = handleRequest(bytes);
                netSocket.write(Buffer.buffer(response));
            });
        });

        server.listen(port, res -> {
            if (res.succeeded()) {
                System.out.println("Server is now listening!");
            } else {
                System.out.println("Failed to start TCP server! :: " + res.cause());
            }
        });
    }

    public static void main(String[] args) {
        VertxTcpServer server = new VertxTcpServer();
        server.doStart(8888);
    }
}
