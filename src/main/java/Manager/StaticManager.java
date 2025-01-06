package Manager;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StaticManager {
    public static List<ExamRecord> getExcel() throws IOException {
        // Excel 檔案的路徑
        File file = new File("D:/oop testfile/test.xlsx");

        // 讀取 Excel 檔案
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(fis);

        // 取得第一個工作表
        Sheet sheet = workbook.getSheetAt(0);

        // 用來存儲所有的資料
        List<ExamRecord> records = new ArrayList<>();

        // 迭代所有的行
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next();  // 跳過表頭

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            // 假設欄位順序是 Name, Test Name, Score, Wrong
            String name = row.getCell(0).getStringCellValue();
            String testName = row.getCell(1).getStringCellValue();
            double score = row.getCell(2).getNumericCellValue();
            String wrong = row.getCell(3).getStringCellValue();

            // 將每一行的資料轉成 ExamRecord 物件
            records.add(new ExamRecord(name, testName, score, wrong));
        }

        // 關閉 Excel 檔案
        workbook.close();

        // 返回解析後的資料
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
