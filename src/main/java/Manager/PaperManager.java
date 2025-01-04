package Manager;

import java.io.*;
import java.util.*;

public class PaperManager {
    private static final String BASE_PATH = "D:\\oop testfile\\";

    public static class Paper {
        public String title;
        boolean isPublic;
        int totalScore;
        List<Question> questions;
        Map<String, Integer> questionScores;

        public Paper() {
            this.questions = new ArrayList<>();
            this.questionScores = new HashMap<>();
        }
    }

    public static class Question {
        String type;
        String content;
        String answer;
        List<String> choices;

        public Question() {
            this.choices = new ArrayList<>();
        }
    }

    public boolean savePaper(String examName, String questions, String answers, String settings) {
        String examPath = BASE_PATH + examName;
        File examDir = new File(examPath);
        System.out.println("Creating directory at: " + examPath);

        File baseDir = new File(BASE_PATH);
        if (!baseDir.exists()) {
            baseDir.mkdirs();

        }
        if (!examDir.exists()) {
            examDir.mkdirs();
        }


        try {
            writeFile(examPath + "\\" + examName + "_Question.txt", questions);
            writeFile(examPath + "\\" + examName + "_Answer.txt", answers);
            writeFile(examPath + "\\" + examName + "_Setting.txt", settings);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Paper getPaper(String examName) {
        String examPath = BASE_PATH + examName;
        Paper paper = new Paper();
        paper.title = examName;

        try {
            List<String> settings = readFile(examPath + "\\" + examName + "_Setting.txt");
            parseSettings(settings, paper);

            List<String> questions = readFile(examPath + "\\" + examName + "_Question.txt");
            List<String> answers = readFile(examPath + "\\" + examName + "_Answer.txt");
            parseQuestions(questions, answers, paper);

            return paper;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void parseSettings(List<String> settings, Paper paper) {
        for (int i = 0; i < settings.size(); i++) {
            String line = settings.get(i);
            if (line.startsWith("Exam Type:")) {
                paper.isPublic = line.contains("Public");
            } else if (line.startsWith("Total Score:")) {
                paper.totalScore = Integer.parseInt(line.split(":")[1].trim());
            } else if (line.startsWith("Question:")) {
                String question = line.split(":")[1].trim();
                String scoreLine = settings.get(i + 1);
                if (scoreLine.startsWith("Score:")) {
                    int score = Integer.parseInt(scoreLine.split(":")[1].trim());
                    paper.questionScores.put(question, score);
                }
            }
        }
    }

    private void parseQuestions(List<String> questionLines, List<String> answerLines, Paper paper) {
        Question currentQuestion = null;

        for (String line : questionLines) {
            if (line.trim().isEmpty()) continue;

            if (line.contains("Question:")) {
                if (currentQuestion != null) {
                    paper.questions.add(currentQuestion);
                }
                currentQuestion = new Question();
                String[] parts = line.split("Question:", 2);
                currentQuestion.type = parts[0].trim();
                currentQuestion.content = parts[1].trim();
            } else if (line.startsWith("choice:")) {
                String[] choices = line.substring(7).split(",");
                currentQuestion.choices = Arrays.asList(choices);
            }
        }
        if (currentQuestion != null) {
            paper.questions.add(currentQuestion);
        }

        currentQuestion = null;
        for (String line : answerLines) {
            if (line.startsWith("Question:")) {
                currentQuestion = findQuestion(paper.questions, line.split(":")[1].trim());
            } else if (line.startsWith("Answer:") && currentQuestion != null) {
                currentQuestion.answer = line.split(":")[1].trim();
            }
        }
    }

    private Question findQuestion(List<Question> questions, String content) {
        for (Question q : questions) {
            if (q.content.equals(content)) {
                return q;
            }
        }
        return null;
    }

    public boolean paperExists(String examName) {
        File examDir = new File(BASE_PATH + examName);
        if (!examDir.exists()) return false;

        return new File(examDir, examName + "_Question.txt").exists() &&
                new File(examDir, examName + "_Answer.txt").exists() &&
                new File(examDir, examName + "_Setting.txt").exists();
    }

    private void writeFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            System.out.println("File written successfully: " + filePath);
            writer.write(content);
        }
    }

    private List<String> readFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    public boolean deletePaper(String examName) {
        File examDir = new File(BASE_PATH + examName);
        if (!examDir.exists()) return false;

        File[] files = examDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.delete()) return false;
            }
        }
        return examDir.delete();
    }

    public List<String> getAllPapers() {
        List<String> papers = new ArrayList<>();
        File baseDir = new File(BASE_PATH);
        File[] dirs = baseDir.listFiles(File::isDirectory);
        if (dirs != null) {
            for (File dir : dirs) {
                if (paperExists(dir.getName())) {
                    papers.add(dir.getName());
                }
            }
        }
        return papers;
    }
}