package com.eventor.university.api;

public class SubmitPartAnswer {
    public final String courseId;
    public final String studentId;
    public final int[] answer;
    public final int part;

    public SubmitPartAnswer(String courseId, String studentId, int[] answer, int part) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.answer = answer;
        this.part = part;
    }
}
