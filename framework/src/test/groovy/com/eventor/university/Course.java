package com.eventor.university;

import com.eventor.api.annotations.*;

@Aggregate
public class Course {
    @Id
    private String id;
    private boolean solved;
    private boolean attempt;

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

    @CommandHandler
    public Object on(@IdIn("courseId") StartCourseExam cmd) {
        if (attempt) {
            throw new RuntimeException("Only one attempt for course exam");
        }
        attempt = true;
        return new CourseExamStarted(cmd.courseId, cmd.studentId);
    }

    @EventHandler
    public void on(Object e) {
    }
}
