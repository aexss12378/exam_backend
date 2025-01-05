package handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
//import java.util.Map;
import Manager.AccountManager;
import utils.HttpUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class AccountHandler implements HttpHandler {
    private final AccountManager accountManager = new AccountManager();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    handleGetRequest(exchange);
                    break;

                case "POST":
                    handlePostRequest(exchange);
                    break;

                default:
                    HttpUtils.sendErrorResponse(exchange, "Method not allowed");
                    exchange.getResponseHeaders().add("Allow", "GET, POST");
            }
        } catch (Exception e) {
            HttpUtils.sendErrorResponse(exchange, "Internal server error");
            e.printStackTrace(); // 或使用日誌記錄工具記錄
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String username = parseQueryParam(exchange, "username");
        if (username == null || username.isEmpty()) {
            HttpUtils.sendErrorResponse(exchange, "Username is required");
            return;
        }

        try {
            String response = accountManager.getAccountInfo(username).toString();
            HttpUtils.sendResponse(exchange, response);
        } catch (IllegalArgumentException e) {
            HttpUtils.sendErrorResponse(exchange, e.getMessage());
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String requestBody = HttpUtils.readRequestBody(exchange);

        try {
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
            String requestUsername = jsonObject.get("username").getAsString();
            String password = jsonObject.get("password").getAsString();

            if (requestUsername == null || password == null || requestUsername.isEmpty() || password.isEmpty()) {
                HttpUtils.sendErrorResponse(exchange, "Invalid input. 'username' and 'password' are required.");
                return;
            }

            String result = accountManager.createAccount(requestUsername, password);
            HttpUtils.sendResponse(exchange, result);
        } catch (JsonSyntaxException | IllegalStateException e) {
            HttpUtils.sendErrorResponse(exchange, "Invalid JSON format or missing fields");
        }
    }

    // 工具方法：解析查詢參數中的 username
    private static String parseQueryParam(HttpExchange exchange, String paramName) {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.contains(paramName + "=")) {
            return query.split(paramName + "=")[1].split("&")[0];
        }
        return null;
    }
}
