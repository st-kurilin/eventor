package com.eventor.university;

public class FailedOnCourseResultsSubmission {
    public final String courseId;
    public final String studentId;

    public FailedOnCourseResultsSubmission(String courseId, String studentId) {
        this.courseId = courseId;
        this.studentId = studentId;
    }
}
