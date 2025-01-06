package Manager;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatisticsManager {
    public static List<ExamRecord> getAllExcel() throws IOException {
        return extractRecords(null); // 提取所有記錄
    }

    public static List<ExamRecord> getExcel(String username) throws IOException {
        return extractRecords(username); // 根據 username 提取記錄
    }

    private static List<ExamRecord> extractRecords(String username) throws IOException {
        // Excel 檔案的路徑
        File file = new File("D:/oop testfile/test.xlsx");

        // 讀取 Excel 檔案
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(fis);

        // 取得第一個工作表
        Sheet sheet = workbook.getSheetAt(0);

        // 用來存儲結果的清單
        List<ExamRecord> records = new ArrayList<>();

        // 迭代所有的行
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // 跳過表頭

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            // 假設欄位順序是 Name, Test Name, Score, Wrong
            String name = row.getCell(0).getStringCellValue();
            if (username == null || name.equals(username)) { // 適用於全部或特定用戶
                String testName = row.getCell(1).getStringCellValue();
                double score = row.getCell(2).getNumericCellValue();
                String wrong = row.getCell(3).getStringCellValue();

                records.add(new ExamRecord(name, testName, score, wrong));
            }
        }

        // 關閉 Excel 檔案
        workbook.close();

        return records;
    }

    // 定義一個資料類來存儲每行的資料
    public static class ExamRecord {
        private String name;
        private String testName;
        private double score;
        private String wrong;

        public ExamRecord(String name, String testName, double score, String wrong) {
            this.name = name;
            this.testName = testName;
            this.score = score;
            this.wrong = wrong;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getWrong() {
            return wrong;
        }

        public void setWrong(String wrong) {
            this.wrong = wrong;
        }
    }
}
