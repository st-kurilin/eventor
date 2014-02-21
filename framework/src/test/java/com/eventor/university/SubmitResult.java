package com.eventor.university;

public class SubmitResult {
    String examId;
    String studentId;
    int[] answers;

    public SubmitResult(String examId, String studentId, int[] answers) {
        this.examId = examId;
        this.studentId = studentId;
        this.answers = answers;
    }
}
