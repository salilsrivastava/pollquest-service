package org.bits.ss;

import com.sun.net.httpserver.HttpServer;
import org.bits.ss.handlers.RestHandler;
import org.bits.ss.service.PGService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static final String APP_NAME = "pollquest-service";
    public static final int PORT = 8008;
    static HttpServer server;

    private static PGService pgService;

    public static void main(String[] args) throws IOException {
        startServer(PORT);
        pgService = new PGService();
        pgService.initDataSource();
        logger.info("isDBConnected" + pgService.isDBConnected());
        pgService.initFlyway();
        Runtime.getRuntime().addShutdownHook(new Thread(Main::stopServer));
    }

    public static void startServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/" + APP_NAME + "/", new RestHandler());
        server.start();
        logger.info(String.format("Server started at : http://localhost:%d/%s", port, APP_NAME));
    }

    public static void stopServer(){
        server.stop(10);
        logger.info("Server stopped");
    }
}