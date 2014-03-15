package com.eventor.university.api;

public class FinalExamSubmitted {
    public final String courseId;
    public final String studentId;
    public final int result;

    public FinalExamSubmitted(String courseId, String studentId, int result) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.result = result;
    }
}
