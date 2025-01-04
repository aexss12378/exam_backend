package handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
//import java.util.Map;
import Manager.AccountManager;
import utils.HttpUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AccountHandler implements HttpHandler {
    private final AccountManager accountManager = new AccountManager();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                // 從查詢參數中獲取 username
                String username = parseQueryParam(exchange, "username");
                if (username == null || username.isEmpty()) {
                    HttpUtils.sendErrorResponse(exchange, "Username is required");
                    return;
                }

                try {
                    // 調用accountManage的 getAccountInfo 方法
                    String response = accountManager.getAccountInfo(username).toString();
                    HttpUtils.sendResponse(exchange, response);
                } catch (IllegalArgumentException e) {
                    HttpUtils.sendErrorResponse(exchange, e.getMessage());
                }
                break;

            case "POST":
                // 讀取請求內容作為 JSON 格式資料
                String requestBody = HttpUtils.readRequestBody(exchange);

                try {
                    // 使用 Gson 解析 JSON 請求
                    JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
                    String requestUsername  = jsonObject.get("username").getAsString();
                    String password = jsonObject.get("password").getAsString();

                    // 假設 "username" 和 "password" 是必須的字段
                    if (requestUsername  == null || password == null || requestUsername .isEmpty() || password.isEmpty()) {
                        HttpUtils.sendErrorResponse(exchange, "Invalid input. 'username' and 'password' are required.");
                        return;
                    }

                    // 調用 accountManager 方法創建帳戶
                    String result = accountManager.createAccount(requestUsername , password);
                    HttpUtils.sendResponse(exchange, result);
                } catch (Exception e) {
                    HttpUtils.sendErrorResponse(exchange, "Invalid JSON format or missing fields");
                }
                break;

            default:
            HttpUtils.sendErrorResponse(exchange, "Method not allowed");
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
