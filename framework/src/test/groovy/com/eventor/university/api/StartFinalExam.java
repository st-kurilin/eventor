package com.eventor.university.api;

public class StartFinalExam {
    public final String courseId;
    public final String studentId;

    public StartFinalExam(String courseId, String studentId) {
        this.courseId = courseId;
        this.studentId = studentId;
    }
}
