package com.eventor.university.api;

public class FinalExamStarted {
    public final String courseId;
    public final String studentId;
    public final int parts;

    public FinalExamStarted(String courseId, String studentId, int parts) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.parts = parts;
    }
}
