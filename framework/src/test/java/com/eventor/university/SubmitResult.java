package com.eventor.university;

public class SubmitResult {
    String courseId;
    String studentId;
    int[] answers;

    public SubmitResult(String courseId, String studentId, int[] answers) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.answers = answers;
    }
}
