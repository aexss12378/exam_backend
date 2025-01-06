package Manager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;

public class ExamManager {
    // 可以添加需要的實例變量
    private final String excelFilePath;

    public ExamManager(String excelFilePath) {
        this.excelFilePath = excelFilePath;
    }

    public void checkAnswer(String fileName) {
        // 讀取文字檔中的結果資訊
        Map<String, Object> result = readResultFromFile(fileName);

        if (result == null) {
            System.err.println("Failed to read result from file: " + fileName);
            return;
        }

        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            Row dataRow = sheet.createRow(lastRowNum + 1);

            dataRow.createCell(0).setCellValue(result.get("Student").toString());
            dataRow.createCell(1).setCellValue(result.get("Exam").toString());
            dataRow.createCell(2).setCellValue(Integer.parseInt(result.get("Total Score").toString()));
            dataRow.createCell(3).setCellValue(result.get("Wrong Questions").toString());

            try (FileOutputStream fos = new FileOutputStream(excelFilePath)) {
                workbook.write(fos);
                System.out.println("Data written to Excel file successfully.");
            }

        } catch (IOException e) {
            System.err.println("Error accessing or writing to Excel file: " + e.getMessage());
        }
    }

    private Map<String, String> loadAnswers(String answerFile) throws IOException {
        Map<String, String> answers = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(answerFile))) {
            String line;
            String questionId = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("考卷標題:") || line.startsWith("考卷類型:") || line.startsWith("總分:")) {
                    continue;
                }

                if (line.startsWith("Question: ")) {
                    questionId = line.substring(10).trim();
                } else if (line.startsWith("Answer: ") && questionId != null) {
                    String answer = line.substring(8).trim();
                    answers.put(questionId, answer);
                    questionId = null;
                }

                if (line.equals("//end")) {
                    break;
                }
            }
        }
        return answers;
    }

    private Map<String, Integer> loadScores(String settingFile) throws IOException {
        Map<String, Integer> scores = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(settingFile))) {
            String line;
            String questionId = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("Exam Title:") || line.startsWith("Exam Type:") || line.startsWith("Total Score:")) {
                    continue;
                }
                if (line.startsWith("Question: ")) {
                    questionId = line.substring(10).trim();
                } else if (line.startsWith("Score:") && questionId != null) {
                    String scoreString = line.substring(6).trim();
                    try {
                        scores.put(questionId, Integer.parseInt(scoreString));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid score format: " + scoreString);
                    }
                    questionId = null;
                }
                if (line.equals("//end")) {
                    break;
                }
            }
        }
        return scores;
    }

    private Map<String, Object> readResultFromFile(String fileName) {
        Map<String, Object> result = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    result.put(key, value);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading result file: " + e.getMessage());
            return null;
        }

        return result;
    }
}