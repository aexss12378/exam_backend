package Manager;

import java.io.*;
import java.util.*;

public class ExamManager {

    static class Question {
        String id;
        String correctAnswer;
        int score;

        public Question(String id, String correctAnswer, int score) {
            this.id = id;
            this.correctAnswer = correctAnswer;
            this.score = score;
        }
    }

    public static Map<String, Object> checkAnswer(String answerPath, String settingPath, String myAnswerPath) throws IOException {
        // File paths
        String answerFile = answerPath;
        String settingFile = settingPath;
        String myAnswerFile = myAnswerPath;
        Map<String, Object> result = new HashMap<>();

        // Load data
        Map<String, String> correctAnswers = loadAnswers(answerFile);
        Map<String, Integer> questionScores = loadScores(settingFile);
        show(correctAnswers, questionScores);

        // Combine answers and scores by QuestionID
        Map<String, Question> questionMap = new LinkedHashMap<>();
        for (String questionId : correctAnswers.keySet()) {
            String correctAnswer = correctAnswers.get(questionId);
            Integer score = questionScores.get(questionId);
            if (score != null) {
                questionMap.put(questionId, new Question(questionId, correctAnswer, score));
            }
        }

        // Load student data
        Map<String, String> studentData = loadStudentData(myAnswerFile);

        // Extract student and exam information
        String studentName = studentData.get("username");
        String examName = studentData.get("examName");

        // Validate extracted data
      

        // Extract student answers
        List<String> studentAnswers = extractStudentAnswers(myAnswerFile);

        // Process and calculate score
        int totalScore = 0;
        
        List<Integer> wrongQuestions = new ArrayList<>();

        System.out.println("--- Exam Comparison Process ---");
        int questionIndex = 0;  // Used to track question order
        for (Map.Entry<String, Question> entry : questionMap.entrySet()) {
            String questionId = entry.getKey();
            Question question = entry.getValue();

            // Directly get student answer by index
            String studentAnswer = questionIndex < studentAnswers.size() ? studentAnswers.get(questionIndex) : "";

            System.out.println("Question ID: " + questionId);
            System.out.println("Correct Answer: " + question.correctAnswer);
            System.out.println("Student Answer: " + studentAnswer);

            if (!studentAnswer.equalsIgnoreCase(question.correctAnswer)) {
                System.out.println("Result: Incorrect");
                wrongQuestions.add(questionIndex+1);
            } else {
                System.out.println("Result: Correct");
                totalScore += question.score;
            }
            System.out.println();

            questionIndex++;
        }

        // Output results
        result.put("Student", studentName);
        result.put("Exam", examName);
        result.put("Total Score", totalScore);
        result.put("Wrong Questions", wrongQuestions);

        return result;
    }

    // Load answers from the answer file
    private static Map<String, String> loadAnswers(String answerFile) throws IOException {
        Map<String, String> answers = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(answerFile))) {
            String line;
            String questionId = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                
                // 跳過不必要的行，包括 "考卷標題:" 和其他標題
                if (line.isEmpty() || line.startsWith("考卷標題:") || line.startsWith("考卷類型:") || line.startsWith("總分:")) {
                    continue;
                }
                
                // 提取 Question 和 Answer
                if (line.startsWith("Question: ")) {
                    questionId = line.substring(10).trim();  // 提取問題 ID
                } else if (line.startsWith("Answer: ") && questionId != null) {
                    String answer = line.substring(8).trim();  // 提取答案
                    answers.put(questionId, answer);  // 儲存問題 ID 和答案
                    questionId = null;  // 重置問題 ID
                }
                
                // 當讀到 //end 時停止
                if (line.equals("//end")) {
                    break;  // 停止處理
                }
            }
        }
        int size = answers.size();
        System.out.println("Map的大小是: " + size);
        return answers;
    }

    // Load scores from the setting file
    private static Map<String, Integer> loadScores(String settingFile) throws IOException {
        Map<String, Integer> scores = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(settingFile))) {
            String line;
            String questionId = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                // Skip unnecessary lines
                if (line.isEmpty() || line.startsWith("Exam Title:") || line.startsWith("Exam Type:") || line.startsWith("Total Score:")) {
                    continue;
                }
                if (line.startsWith("Question: ")) {
                    questionId = line.substring(10).trim();  // Get question ID and trim spaces
                } else if (line.startsWith("Score:") && questionId != null) {
                    // Get score part and trim spaces
                    String scoreString = line.substring(6).trim();
                    try {
                        int score = Integer.parseInt(scoreString);  // Parse score
                        scores.put(questionId, score);  // Store score for the question
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid score format: " + scoreString);  // Catch exception and print error message
                    }
                    questionId = null;  // Reset questionId
                }
                // Stop reading when encountering //end
                if (line.equals("//end")) {
                    break;  // Only stop when encountering //end
                }
            }
        }
        return scores;
    }

    // Load student data (username and exam name)
    private static Map<String, String> loadStudentData(String myAnswerFile) throws IOException {
        Map<String, String> studentData = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(myAnswerFile))) {
            String line = br.readLine();
            if (line != null) {
                int startIdx = line.indexOf("Student:") + 8;  // Find "Student:" position
                int endIdx = line.indexOf("Exam:");  // Find "Exam:" position
                if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
                    String username = line.substring(startIdx, endIdx).trim();
                    String examName = line.substring(endIdx + 5).trim();  // Get exam name
                    studentData.put("username", username);
                    studentData.put("examName", examName);
                }
            }
        }
        return studentData;
    }

    // Extract student answers from the file
    private static List<String> extractStudentAnswers(String myAnswerFile) throws IOException {
        List<String> answers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(myAnswerFile))) {
            String line;
            
            // Skip first line (student information)
            br.readLine();  // Skip first line (Student: John Doe)
    
            // Read answers starting from second line
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
    
                // Each line as one answer
                answers.add(line.trim());
            }
        }
        return answers;
    }

    private static void show(Map<String, String> correctAnswers, Map<String, Integer> questionScores) {
        System.out.println("--- Correct Answers ---");
        for (Map.Entry<String, String> entry : correctAnswers.entrySet()) {
            System.out.println("Question ID: " + entry.getKey() + ", Answer: " + entry.getValue());
        }

        System.out.println("--- Question Scores ---");
        for (Map.Entry<String, Integer> entry : questionScores.entrySet()) {
            System.out.println("Question ID: " + entry.getKey() + ", Score: " + entry.getValue());
        }
    }
}


