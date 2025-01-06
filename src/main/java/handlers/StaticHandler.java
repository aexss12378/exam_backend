package handlers;

import Manager.StaticManager;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import utils.HttpUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class StaticHandler implements HttpHandler {
    //private final StaticHandler sastaticHandler = new StaticHandler();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestURI().getPath()) {
            case "/static":
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

    private void handleGetStatic(HttpExchange exchange) throws IOException {
        try {
            // 調用 StaticManager 的 getExcel 方法獲取資料
            List<StaticManager.ExamRecord> records = StaticManager.getExcel();

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
}
