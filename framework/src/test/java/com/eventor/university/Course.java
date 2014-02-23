package com.eventor.university;

import com.eventor.api.*;

@Aggregate
public class Course {
    @Id
    private String id;
    private boolean solved;

    @EventHandler
    @Start
    public void on(CourseRegistered evt) {
        id = evt.courseId;
    }

    @CommandHandler
    public Object on(@IdIn("courseId") SubmitResult cmd) {
        if (cmd.answers.length != 5) {
            throw new RuntimeException("Validation failed");
        }
        int rightAnswers = 0;
        for (int i = 0; i < cmd.answers.length; i++) {
            if (cmd.answers[i] == 42) {
                rightAnswers++;
            }
        }
        if (rightAnswers == 0) {
            return new FailedOnCourseResultsSubmition(cmd.courseId, cmd.studentId);
        }
        if (solved) {
            return new CourseResultsSubmitted(cmd.courseId, cmd.studentId, rightAnswers * 20);
        } else {
            solved = true;
            return new CourseResultsSubmitted(cmd.courseId, cmd.studentId, rightAnswers * 20 + 20);
        }
    }

    @EventHandler
    public void on(Object e) {
    }
}
