import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class OnlineExaminationSystem {

    private static final String QUESTION_FILE = "exam.txt";
    private static final String RESULT_FILE = "results.txt";

    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<Question> allQuestions = new ArrayList<Question>();
    private static final ArrayList<Candidate> candidates = new ArrayList<Candidate>();

    public static void main(String[] args) {
        try {
            loadQuestionsFromFile();
        } catch (IOException e) {
            System.out.println("Could not read " + QUESTION_FILE + ": " + e.getMessage());
            return;
        }

        if (allQuestions.isEmpty()) {
            System.out.println("No questions found in " + QUESTION_FILE + ".");
            return;
        }

        System.out.println("================================");
        System.out.println("   ONLINE EXAMINATION SYSTEM");
        System.out.println("================================");

        boolean running = true;
        while (running) {
            System.out.println("\n1. Start Exam");
            System.out.println("2. View Rank List");
            System.out.println("3. Exit");
            System.out.print("Enter menu option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    startExam();
                    break;
                case "2":
                    showRankList();
                    break;
                case "3":
                    running = false;
                    System.out.println("Thank you. Program closed.");
                    break;
                default:
                    System.out.println("Invalid menu option. Enter 1, 2, or 3.");
            }
        }
    }

    // Reads questions from exam.txt.
    // Format: id|question|optionA|optionB|optionC|optionD|correctAnswer
    private static void loadQuestionsFromFile() throws IOException {
        Path filePath = Paths.get(QUESTION_FILE);

        if (!Files.exists(filePath)) {
            throw new IOException("File not found. Keep exam.txt in the project folder.");
        }

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

        for (String line : lines) {
            if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                continue;
            }

            String[] parts = line.split("\\|", -1);

            if (parts.length != 7) {
                System.out.println("Invalid line skipped in exam.txt: " + line);
                continue;
            }

            String answer = parts[6].trim().toUpperCase();

            if (!answer.matches("[ABCD]")) {
                System.out.println("Invalid answer key. Line skipped: " + line);
                continue;
            }

            allQuestions.add(new Question(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                parts[4].trim(),
                parts[5].trim(),
                answer
            ));
        }

        System.out.println(allQuestions.size() + " question(s) loaded successfully.");
    }

    private static void startExam() {
        if (allQuestions.isEmpty()) {
            System.out.println("No questions are available.");
            return;
        }

        System.out.print("Candidate name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Candidate name cannot be empty. Exam not started.");
            return;
        }

        Queue<Question> questionQueue = new LinkedList<Question>(allQuestions);
        Stack<Question> answeredStack = new Stack<Question>();
        Set<String> skippedOnce = new HashSet<String>();

        int correct = 0;
        int wrong = 0;
        int questionNumber = 1;

        System.out.println("\nExam started. Enter A, B, C, or D.");
        System.out.println("Enter S to move a question to the end of the queue.");

        while (!questionQueue.isEmpty()) {
            Question q = questionQueue.peek();

            System.out.println("\n" + q.id + ". " + q.question);
            System.out.println("A. " + q.optionA);
            System.out.println("B. " + q.optionB);
            System.out.println("C. " + q.optionC);
            System.out.println("D. " + q.optionD);
            System.out.print("Your answer: ");

            String answer = sc.nextLine().trim().toUpperCase();

            if (answer.equals("S")) {
                if (skippedOnce.contains(q.id)) {
                    System.out.println("This question was already skipped. Please enter A, B, C, or D.");
                    continue;
                }

                questionQueue.poll();
                questionQueue.offer(q);
                skippedOnce.add(q.id);
                System.out.println(q.id + " skipped and moved to the end of the queue.");
                continue;
            }

            if (!answer.matches("[ABCD]")) {
                System.out.println("Invalid option. Enter A, B, C, D or S (skip).");
                continue;
            }

            questionQueue.poll();
            answeredStack.push(q);
            questionNumber++;

            if (answer.equals(q.correctAnswer)) {
                correct++;
                System.out.println("Correct answer! +4 marks.");
            } else {
                wrong++;
                System.out.println("Wrong answer. -1 mark.");
            }
        }

        int marks = (correct * 4) - wrong;
        Candidate candidate = new Candidate(name, correct, wrong, marks, allQuestions.size());
        candidates.add(candidate);

        System.out.println("\nExam submitted. " + name
                + " | Correct: " + correct + " | Wrong: " + wrong);
        System.out.println("Marks: " + marks + "/" + (allQuestions.size() * 4));
        System.out.println("Answered questions stored in stack: " + answeredStack.size());

        try {
            saveResults();
        } catch (IOException e) {
            System.out.println("Could not save results: " + e.getMessage());
        }
    }

    // Sorts candidates by marks in descending order.
    private static void showRankList() {
        if (candidates.isEmpty()) {
            System.out.println("No exam results available yet.");
            return;
        }

        Collections.sort(candidates, new Comparator<Candidate>() {
            @Override
            public int compare(Candidate first, Candidate second) {
                return Integer.compare(second.marks, first.marks);
            }
        });

        System.out.println("\n========== RANK LIST ==========");
        for (int i = 0; i < candidates.size(); i++) {
            Candidate c = candidates.get(i);
            System.out.println((i + 1) + ". " + c.name + " | " + c.marks);
        }
    }

    // Saves the current rank list in results.txt.
    private static void saveResults() throws IOException {
        Collections.sort(candidates, new Comparator<Candidate>() {
            @Override
            public int compare(Candidate first, Candidate second) {
                return Integer.compare(second.marks, first.marks);
            }
        });

        List<String> output = new ArrayList<String>();
        output.add("RANK LIST");
        for (int i = 0; i < candidates.size(); i++) {
            Candidate c = candidates.get(i);
            output.add((i + 1) + ". " + c.name
                    + " | Correct: " + c.correct
                    + " | Wrong: " + c.wrong
                    + " | Marks: " + c.marks + "/" + (c.totalQuestions * 4));
        }

        Files.write(Paths.get(RESULT_FILE), output, StandardCharsets.UTF_8);
        System.out.println("Results saved in " + RESULT_FILE + ".");
    }

    private static class Question {
        String id;
        String question;
        String optionA;
        String optionB;
        String optionC;
        String optionD;
        String correctAnswer;

        Question(String id, String question, String optionA, String optionB,
                 String optionC, String optionD, String correctAnswer) {
            this.id = id;
            this.question = question;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.correctAnswer = correctAnswer;
        }
    }

    private static class Candidate {
        String name;
        int correct;
        int wrong;
        int marks;
        int totalQuestions;

        Candidate(String name, int correct, int wrong, int marks, int totalQuestions) {
            this.name = name;
            this.correct = correct;
            this.wrong = wrong;
            this.marks = marks;
            this.totalQuestions = totalQuestions;
        }
    }
}
