package handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;

import Manager.ExamManager;
import utils.HttpUtils;
import com.google.gson.Gson;

public class ExamHandler implements HttpHandler {
    private final ExamManager examManager;
    private final Gson gson;

    public ExamHandler(String excelFilePath) {
        this.examManager = new ExamManager(excelFilePath);
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestURI().getPath()) {
            case "/exams":
                if ("POST".equals(exchange.getRequestMethod())) {
                    handleCheckAnswer(exchange);
                } else {
                    HttpUtils.sendErrorResponse(exchange, "Method not allowed for /exams");
                }
                break;

            default:
                HttpUtils.sendErrorResponse(exchange, "Unknown endpoint");
        }
    }

    private void handleCheckAnswer(HttpExchange exchange) throws IOException {
        try {
            String requestBody = HttpUtils.readRequestBody(exchange);
            ExamSubmission submission = gson.fromJson(requestBody, ExamSubmission.class);

            if (submission == null || submission.getFileName() == null || submission.getFileName().isEmpty()) {
                HttpUtils.sendErrorResponse(exchange, "無效的提交數據");
                return;
            }

            examManager.checkAnswer(submission.getFileName());
            String response = gson.toJson(new SuccessResponse("考試檢查完成"));
            HttpUtils.sendResponse(exchange, response);

        } catch (Exception e) {
            e.printStackTrace();
            HttpUtils.sendErrorResponse(exchange, "處理考試提交時發生錯誤: " + e.getMessage());
        }
    }

    private static class ExamSubmission {
        private String fileName;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }

    private static class SuccessResponse {
        private final String message;

        public SuccessResponse(String message) {
            this.message = message;
        }
    }
}