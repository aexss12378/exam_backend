package handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import Manager.AccountManager;
import utils.HttpUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.util.Map;

public class AccountHandler implements HttpHandler {
    private final AccountManager accountManager = new AccountManager();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            // 根據路徑分發請求
            switch (path) {
                case "/account/login":
                    if (!method.equals("POST")) {
                        HttpUtils.sendErrorResponse(exchange, "Method not allowed");
                        exchange.getResponseHeaders().add("Allow", "POST");
                        return;
                    }
                    handleLogin(exchange);
                    break;

                case "/account/create":
                    if (!method.equals("POST")) {
                        HttpUtils.sendErrorResponse(exchange, "Method not allowed");
                        exchange.getResponseHeaders().add("Allow", "POST");
                        return;
                    }
                    handleCreateAccount(exchange);
                    break;

                case "/account/info":
                    if (!method.equals("GET")) {
                        HttpUtils.sendErrorResponse(exchange, "Method not allowed");
                        exchange.getResponseHeaders().add("Allow", "GET");
                        return;
                    }
                    handleGetAccountInfo(exchange);
                    break;

                default:
                    HttpUtils.sendErrorResponse(exchange, "Path not found");
            }
        } catch (Exception e) {
            HttpUtils.sendErrorResponse(exchange, "Internal server error");
            e.printStackTrace();
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        String requestBody = HttpUtils.readRequestBody(exchange);

        try {
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
            String username = jsonObject.get("username").getAsString();
            String password = jsonObject.get("password").getAsString();

            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                HttpUtils.sendErrorResponse(exchange, "Invalid input. 'username' and 'password' are required.");
                return;
            }

            boolean isAuthenticated = accountManager.authenticate(username, password);
            JsonObject response = new JsonObject();

            if (isAuthenticated) {
                response.addProperty("status", "success");
                response.addProperty("message", "Login successful");
                HttpUtils.sendResponse(exchange, response.toString());
            } else {
                response.addProperty("status", "error");
                response.addProperty("message", "Invalid username or password");
                HttpUtils.sendResponse(exchange, response.toString());
            }
        } catch (JsonSyntaxException | IllegalStateException e) {
            HttpUtils.sendErrorResponse(exchange, "Invalid JSON format or missing fields");
        }
    }

    private void handleCreateAccount(HttpExchange exchange) throws IOException {
        String requestBody = HttpUtils.readRequestBody(exchange);

        try {
            JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
            String username = jsonObject.get("username").getAsString();
            String password = jsonObject.get("password").getAsString();
            String role = jsonObject.get("role").getAsString();

            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                HttpUtils.sendErrorResponse(exchange, "Invalid input. 'username' and 'password' are required.");
                return;
            }

            String result = accountManager.createAccount(username, password, role);
            JsonObject response = new JsonObject();
            response.addProperty("status", result.contains("successfully") ? "success" : "error");
            response.addProperty("message", result);
            HttpUtils.sendResponse(exchange, response.toString());
        } catch (JsonSyntaxException | IllegalStateException e) {
            HttpUtils.sendErrorResponse(exchange, "Invalid JSON format or missing fields");
        }
    }

    private void handleGetAccountInfo(HttpExchange exchange) throws IOException {
        String username = parseQueryParam(exchange, "username");
        if (username == null || username.isEmpty()) {
            HttpUtils.sendErrorResponse(exchange, "Username is required");
            return;
        }

        try {
            Map<String, String> accountInfo = accountManager.getAccountInfo(username);
            JsonObject response = new JsonObject();
            response.addProperty("status", "success");
            JsonObject data = new JsonObject();
            for (Map.Entry<String, String> entry : accountInfo.entrySet()) {
                data.addProperty(entry.getKey(), entry.getValue());
            }
            response.add("data", data);
            HttpUtils.sendResponse(exchange, response.toString());
        } catch (IllegalArgumentException e) {
            JsonObject response = new JsonObject();
            response.addProperty("status", "error");
            response.addProperty("message", e.getMessage());
            HttpUtils.sendResponse(exchange, response.toString());
        }
    }

    private static String parseQueryParam(HttpExchange exchange, String paramName) {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.contains(paramName + "=")) {
            return query.split(paramName + "=")[1].split("&")[0];
        }
        return null;
    }
}