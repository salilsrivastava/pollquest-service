package org.bits.ss;

import com.sun.net.httpserver.HttpServer;
import org.bits.ss.dao.PollQuestionDAO;
import org.bits.ss.handlers.RestHandler;
import org.bits.ss.service.PGService;
import org.bits.ss.service.PollQuestionService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static final String APP_NAME = "pollquest-service";
    public static final int PORT = 8008;
    static HttpServer server;

    private static PGService pgService;
    private static PollQuestionDAO pollQuestionDAO;
    private static PollQuestionService pollQuestionService;
    private static RestHandler restHandler;

    public static void main(String[] args) throws IOException {
        logger.info("Connecting to Database...");
        pgService = new PGService();
        pgService.initDataSource();
        logger.info("Connected to Database...");
        logger.info("isDBConnected" + pgService.isDBConnected());
        pgService.initFlyway();
        pollQuestionDAO = new PollQuestionDAO(pgService);
        pollQuestionService = new PollQuestionService(pollQuestionDAO);
        restHandler = new RestHandler(pollQuestionService);
        startServer(PORT, restHandler);
        Runtime.getRuntime().addShutdownHook(new Thread(Main::stopServer));
    }

    public static void startServer(int port, RestHandler restHandler) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/" + APP_NAME + "/", restHandler);
        server.start();
        logger.info(String.format("Server started at : http://localhost:%d/%s", port, APP_NAME));
    }

    public static void stopServer(){
        server.stop(2);
        logger.info("Server stopped");
    }
}