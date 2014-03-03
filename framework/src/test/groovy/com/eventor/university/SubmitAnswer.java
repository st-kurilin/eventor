package com.eventor.university;

public class SubmitAnswer {
    public final String courseId;
    public final String studentId;
    public final int[] answer;

    public SubmitAnswer(String courseId, String studentId, int[] answer) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.answer = answer;
    }
}
