package com.eventor.university;

public class CourseExamStarted {
    String courseId;
    String studentId;

    public CourseExamStarted(String courseId, String studentId) {
        this.courseId = courseId;
        this.studentId = studentId;
    }
}
