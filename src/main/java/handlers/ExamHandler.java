package handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

import Manager.ExamManager;
import utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.Map;

public class ExamHandler implements HttpHandler {
    private final ExamManager examManager = new ExamManager(); // 使用實例
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestURI().getPath()) {
            case "/exams":
                if ("POST".equals(exchange.getRequestMethod())) {
                    handleCheckAnswer(exchange);
                } else {
                    HttpUtils.sendErrorResponse(exchange, "Method not allowed for /exams"); // 修正錯誤訊息
                }
                break;

            default:
                HttpUtils.sendErrorResponse(exchange, "Unknown endpoint");
        }
    }

    private void handleCheckAnswer(HttpExchange exchange) throws IOException {
        try {
            // 解析請求的 body 資料
            String requestBody = HttpUtils.readRequestBody(exchange);
            CheckAnswerRequest request = gson.fromJson(requestBody, CheckAnswerRequest.class);

            // 空值檢查
            if (request.getAnswerPath() == null || request.getSettingPath() == null || request.getMyAnswerPath() == null) {
                HttpUtils.sendErrorResponse(exchange, "Invalid request: Missing required fields");
                return;
            }

            // 使用實例方法進行檢查
            Map<String, Object> result = examManager.checkAnswer(
                    request.getAnswerPath(),
                    request.getSettingPath(),
                    request.getMyAnswerPath()
            );

            // 回傳結果
            String jsonResponse = gson.toJson(result);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
            exchange.getResponseBody().write(jsonResponse.getBytes());
            exchange.getResponseBody().close();
        } catch (JsonSyntaxException e) {
            e.printStackTrace(); // 提供詳細的錯誤資訊（開發用）
            HttpUtils.sendErrorResponse(exchange, "Invalid JSON format: " + e.getMessage());
        } catch (IOException e) {
            HttpUtils.sendErrorResponse(exchange, "Error processing the request");
        }
    }

    // CheckAnswerRequest 內部類別
    private static class CheckAnswerRequest {
        private String answerPath;
        private String settingPath;
        private String myAnswerPath;

        public String getAnswerPath() {
            return answerPath;
        }

        public void setAnswerPath(String answerPath) {
            this.answerPath = answerPath;
        }

        public String getSettingPath() {
            return settingPath;
        }

        public void setSettingPath(String settingPath) {
            this.settingPath = settingPath;
        }

        public String getMyAnswerPath() {
            return myAnswerPath;
        }

        public void setMyAnswerPath(String myAnswerPath) {
            this.myAnswerPath = myAnswerPath;
        }
    }
}
