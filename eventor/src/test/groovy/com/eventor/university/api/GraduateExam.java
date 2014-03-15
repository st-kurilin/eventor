package com.eventor.university.api;

public class GraduateExam {
    public final String courseId;
    public final String studentId;
    public final int[][] answers;

    public GraduateExam(String courseId, String studentId, int[][] answers) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.answers = answers;
    }
}
