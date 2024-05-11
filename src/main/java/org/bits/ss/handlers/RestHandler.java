package org.bits.ss.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bits.ss.Main;
import org.bits.ss.service.PollQuestionService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RestHandler implements HttpHandler {
    private static final Logger logger = Logger.getLogger(RestHandler.class.getName());
    private final PollQuestionService pollQuestionService;
    ObjectMapper objectMapper = new ObjectMapper();

    public RestHandler(PollQuestionService pollQuestionService) {
        this.pollQuestionService = pollQuestionService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        logger.info(".....Incoming Request.....");
        String method = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();

        if ("GET".equals(method)) {
            Map<String, String> queryMap = parseQueryString(exchange.getRequestURI().getQuery());
            if (requestPath.contains("/api/v1/questions")) {
                String response = pollQuestionService.getQuestions(queryMap.get("id"));
                sendResponse(exchange, response);
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
            }
        } else if ("POST".equals(method)) {
            if (requestPath.contains("/api/v1/addQuestion")) {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                var jsonBody = objectMapper.readValue(requestBody, Questions.class);
                boolean response = pollQuestionService.addQuestion(jsonBody.questionId(), jsonBody.question());
                sendResponse(exchange, Boolean.toString(response));
            } else if (requestPath.contains("/api/v1/generateCode")) {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                var jsonBody = objectMapper.readValue(requestBody, Questions.class);
                String response = pollQuestionService.generateCode(jsonBody.description());
                sendResponse(exchange, response);
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
            }
        } else {
            exchange.sendResponseHeaders(405, 0);
            exchange.close();
        }
    }

    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        sendResponse(exchange, response, 200);
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public Map<String, String> parseQueryString(String queryString) {
        return Arrays.stream(queryString.split("&"))
                .map(pair -> pair.split("="))
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue.length > 1 ? keyValue[1] : "",
                        (value1, value2) -> value1));
    }

    @JsonSerialize
    public record Questions(String questionId, String question, String description) {

    }
}
