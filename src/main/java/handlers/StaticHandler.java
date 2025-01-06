package handlers;

import Manager.StaticManager;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import utils.HttpUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class StaticHandler implements HttpHandler {
    //private final StaticHandler staticHandler = new StaticHandler();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestURI().getPath()) {
            case "/static/getAll":
                if ("GET".equals(exchange.getRequestMethod())) {
                    handleGetAllStatic(exchange);
                } else {
                    HttpUtils.sendErrorResponse(exchange, "Method not allowed for /paper/get");
                }
                break;

            case "/static/get":
                if ("GET".equals(exchange.getRequestMethod())) {
                    handleGetStatic(exchange);
                } else {
                    HttpUtils.sendErrorResponse(exchange, "Method not allowed for /paper/get");
                }
                break;

            default:
                HttpUtils.sendErrorResponse(exchange, "Unknown endpoint");
        }
    }

    private void handleGetAllStatic(HttpExchange exchange) throws IOException {
        try {
            // 調用 StaticManager 的 getAllExcel 方法獲取資料
            List<StaticManager.ExamRecord> records = StaticManager.getAllExcel();

            // 轉換資料為 JSON 格式
            StringBuilder jsonResponse = new StringBuilder("[");
            for (int i = 0; i < records.size(); i++) {
                StaticManager.ExamRecord record = records.get(i);
                jsonResponse.append("{")
                        .append("\"name\": \"").append(record.getName()).append("\", ")
                        .append("\"testName\": \"").append(record.getTestName()).append("\", ")
                        .append("\"score\": ").append(record.getScore()).append(", ")
                        .append("\"wrong\": \"").append(record.getWrong()).append("\"")
                        .append("}");
                if (i < records.size() - 1) {
                    jsonResponse.append(", ");
                }
            }
            jsonResponse.append("]");

            // 設置響應頭部，指定返回 JSON 格式
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.length());

            // 寫入響應內容
            OutputStream os = exchange.getResponseBody();
            os.write(jsonResponse.toString().getBytes());
            os.close();
        } catch (Exception e) {
            // 錯誤處理，發送錯誤響應
            HttpUtils.sendErrorResponse(exchange, "Error reading Excel file");
        }
    }

    private void handleGetStatic(HttpExchange exchange) throws IOException {
        try {
            // 從 URL 中提取參數 username
            String query = exchange.getRequestURI().getQuery();
            String username = getQueryParam(query, "username");

            if (username == null || username.isEmpty()) {
                HttpUtils.sendErrorResponse(exchange, "Missing 'username' parameter");
                return;
            }

            // 調用 StaticManager.getAllExcel 過濾 username 對應的資料
            List<StaticManager.ExamRecord> allRecords = StaticManager.getAllExcel();
            List<StaticManager.ExamRecord> userRecords = new ArrayList<>();
            for (StaticManager.ExamRecord record : allRecords) {
                if (record.getName().equals(username)) {
                    userRecords.add(record);
                }
            }

            // 如果沒有找到資料，返回 404
            if (userRecords.isEmpty()) {
                HttpUtils.sendErrorResponse(exchange, "No records found for user: " + username);
                return;
            }

            // 轉換資料為 JSON 格式
            StringBuilder jsonResponse = new StringBuilder("[");
            for (int i = 0; i < userRecords.size(); i++) {
                StaticManager.ExamRecord record = userRecords.get(i);
                jsonResponse.append("{")
                        .append("\"name\": \"").append(record.getName()).append("\", ")
                        .append("\"testName\": \"").append(record.getTestName()).append("\", ")
                        .append("\"score\": ").append(record.getScore()).append(", ")
                        .append("\"wrong\": \"").append(record.getWrong()).append("\"")
                        .append("}");
                if (i < userRecords.size() - 1) {
                    jsonResponse.append(", ");
                }
            }
            jsonResponse.append("]");

            // 設置響應頭部，指定返回 JSON 格式
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.length());

            // 寫入響應內容
            OutputStream os = exchange.getResponseBody();
            os.write(jsonResponse.toString().getBytes());
            os.close();
        } catch (Exception e) {
            // 錯誤處理，發送錯誤響應
            HttpUtils.sendErrorResponse(exchange, "Error processing request: " + e.getMessage());
        }
    }

    // 在 Handler 內部新增工具方法 getQueryParam
    private String getQueryParam(String query, String paramName) {
        if (query == null || query.isEmpty()) {
            return null;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                return keyValue[1];
            }
        }
        return null;
    }


}
