package org.bits.ss;

import com.sun.net.httpserver.HttpServer;
import org.bits.ss.handlers.RestHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static final String APP_NAME = "pollquest-service";
    public static final int PORT = 8008;
    static HttpServer server;

    public static void main(String[] args) throws IOException {
        startServer(PORT);

    }

    public static void startServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/" + APP_NAME + "/", new RestHandler());
        server.start();
        logger.info(String.format("Server started at : http://localhost:%d/%s", port, APP_NAME));
        Runtime.getRuntime().addShutdownHook(new Thread(Main::stopServer));
    }

    public static void stopServer(){
        server.stop(10);
        logger.info("Server stopped");
    }
}