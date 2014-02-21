package com.eventor.university;

public class ExamResultsSubmitted extends Object {
    public final String examId;
    public final String studentId;
    public final int result;

    public ExamResultsSubmitted(String examId, String studentId, int result) {
        this.examId = examId;
        this.studentId = studentId;
        this.result = result;
    }
}
