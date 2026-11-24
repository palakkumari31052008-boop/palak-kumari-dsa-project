# Online Examination System (Java)

## Files
- `OnlineExaminationSystem.java` — complete Java console application
- `exam.txt` — questions and answer key
- `results.txt` — created automatically after an exam is submitted

## Requirements
- JDK 8 or later
- Keep all project files in the same folder.

## Run in VS Code terminal
Open this project folder in VS Code, then run:

```bash
javac OnlineExaminationSystem.java
java OnlineExaminationSystem
```

## Menu
1. Start Exam
2. View Rank List
3. Exit

## Exam rules
- Correct answer: +4 marks
- Wrong answer: -1 mark
- `S`: moves the current question to the end of the queue
- A question can be skipped once; if it comes around again, enter A, B, C, or D.
- Invalid input does not change the score or remove the question.

## Question file format
Each question must be on one line:

`id|question|optionA|optionB|optionC|optionD|correctAnswer`

The answer key must be A, B, C, or D. Do not use the `|` character inside question or option text.

## Data structures used
- `Queue<Question>`: manages questions and moves skipped questions to the end.
- `Stack<Question>`: stores answered questions.
- `ArrayList<Candidate>`: stores candidate results.
- `Collections.sort()` with a comparator: sorts candidates by marks.
- File handling: reads `exam.txt` and writes `results.txt`.

Note: Results are kept in memory while the program is running. The `results.txt` file is written after each submission; previously saved results are not loaded back into the rank list when the program is restarted.
