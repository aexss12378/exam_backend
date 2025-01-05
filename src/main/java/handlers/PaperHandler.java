package handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import Manager.PaperManager;
import utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.net.URI;

public class PaperHandler implements HttpHandler {
    private final PaperManager paperManager = new PaperManager();
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestURI().getPath()) {
            case "/paper/get":
                if ("GET".equals(exchange.getRequestMethod())) {
                    handleGetPaper(exchange);
                } else {
                    HttpUtils.sendErrorResponse(exchange, "Method not allowed for /paper/get");
                }
                break;

            case "/paper/getAll":
                if ("GET".equals(exchange.getRequestMethod())) {
                    handleGetAllPapers(exchange);
                } else {
                    HttpUtils.sendErrorResponse(exchange, "Method not allowed for /paper/getAll");
                }
                break;

            case "/paper/save":
                if ("POST".equals(exchange.getRequestMethod())) {
                    handleSavePaper(exchange);
                } else {
                    HttpUtils.sendErrorResponse(exchange, "Method not allowed for /paper/save");
                }
                break;

            case "/paper/delete":
                if ("DELETE".equals(exchange.getRequestMethod())) {
                    handleDeletePaper(exchange);
                } else {
                    HttpUtils.sendErrorResponse(exchange, "Method not allowed for /paper/delete");
                }
                break;

            case "/paper/exists":
                if ("HEAD".equals(exchange.getRequestMethod())) {
                    handlePaperExists(exchange);
                } else {
                    HttpUtils.sendErrorResponse(exchange, "Method not allowed for /paper/exists");
                }
                break;

            default:
                HttpUtils.sendErrorResponse(exchange, "Unknown endpoint");
        }
    }

    private String getExamNameFromQuery(HttpExchange exchange) {
        URI uri = exchange.getRequestURI();
        String query = uri.getQuery();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2 && "examName".equals(keyValue[0])) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }

    private void handleGetPaper(HttpExchange exchange) throws IOException {
        try {
            String examName = getExamNameFromQuery(exchange);
            if (examName == null || examName.trim().isEmpty()) {
                HttpUtils.sendErrorResponse(exchange, "examName parameter is required");
                return;
            }

            PaperManager.Paper paper = paperManager.getPaper(examName);
            if (paper != null) {
                String response = gson.toJson(paper);
                HttpUtils.sendResponse(exchange, response);
            } else {
                HttpUtils.sendErrorResponse(exchange, "Paper not found");
            }
        } catch (Exception e) {
            HttpUtils.sendErrorResponse(exchange, "Failed to fetch paper: " + e.getMessage());
        }
    }

    private void handleGetAllPapers(HttpExchange exchange) throws IOException {
        try {
            // 獲取所有試卷名稱
            List<String> paperNames = paperManager.getAllPapers();

            // 將試卷名稱轉換為試卷物件
            List<PaperManager.Paper> papers = new ArrayList<>();
            for (String paperName : paperNames) {
                PaperManager.Paper paper = paperManager.getPaper(paperName);
                if (paper != null) {
                    papers.add(paper);
                }
            }

            // 轉換為 JSON 並發送回應
            String response = gson.toJson(papers);
            HttpUtils.sendResponse(exchange, response);
        } catch (Exception e) {
            HttpUtils.sendErrorResponse(exchange, "Failed to fetch papers: " + e.getMessage());
        }
    }

    private void handleSavePaper(HttpExchange exchange) throws IOException {
        try {
            String requestBody = HttpUtils.readRequestBody(exchange);
            PaperManager.Paper paper = gson.fromJson(requestBody, PaperManager.Paper.class);

            // 修改此處直接使用 paper.title
            if (paper == null || paper.title == null || paper.title.trim().isEmpty()) {
                HttpUtils.sendErrorResponse(exchange, "Invalid paper data: Missing exam title");
                return;
            }

            boolean success = paperManager.savePaper(paper.title, "questions", "answers", "settings");
            if (success) {
                HttpUtils.sendResponse(exchange, "Paper saved successfully");
            } else {
                HttpUtils.sendErrorResponse(exchange, "Failed to save paper");
            }
        } catch (JsonSyntaxException e) {
            HttpUtils.sendErrorResponse(exchange, "Invalid JSON format: " + e.getMessage());
        } catch (Exception e) {
            HttpUtils.sendErrorResponse(exchange, "Failed to save paper: " + e.getMessage());
        }
    }

    private void handleDeletePaper(HttpExchange exchange) throws IOException {
        try {
            String examName = getExamNameFromQuery(exchange);
            if (examName == null || examName.trim().isEmpty()) {
                HttpUtils.sendErrorResponse(exchange, "examName parameter is required");
                return;
            }

            boolean success = paperManager.deletePaper(examName);
            if (success) {
                HttpUtils.sendResponse(exchange, "Paper deleted successfully");
            } else {
                HttpUtils.sendErrorResponse(exchange, "Paper not found or could not be deleted");
            }
        } catch (Exception e) {
            HttpUtils.sendErrorResponse(exchange, "Failed to delete paper: " + e.getMessage());
        }
    }

    private void handlePaperExists(HttpExchange exchange) throws IOException {
        try {
            String examName = getExamNameFromQuery(exchange);
            if (examName == null || examName.trim().isEmpty()) {
                exchange.sendResponseHeaders(400, -1);
                exchange.close();
                return;
            }

            boolean exists = paperManager.paperExists(examName);
            exchange.sendResponseHeaders(exists ? 200 : 404, -1);
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, -1);
        } finally {
            exchange.close();
        }
    }

}