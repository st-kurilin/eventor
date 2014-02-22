package com.eventor.university;

public class ExamResultsSubmitted extends Object {
    public final String courseId;
    public final String studentId;
    public final int result;

    public ExamResultsSubmitted(String courseId, String studentId, int result) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.result = result;
    }
}
