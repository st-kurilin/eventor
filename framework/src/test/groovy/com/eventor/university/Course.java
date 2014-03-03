package com.eventor.university;

import com.eventor.api.annotations.*;

@Aggregate
public class Course {
    @Id
    private String id;

    private boolean solved;

    @EventListener
    @Start
    public void on(CourseRegistered evt) {
        id = evt.courseId;
    }

    @CommandHandler
    public Object on(@IdIn("courseId") SubmitAnswer cmd) {
        if (cmd.answer.length != 5) {
            throw new RuntimeException("Validation failed");
        }
        int rightAnswers = 0;
        for (int i = 0; i < cmd.answer.length; i++) {
            if (cmd.answer[i] == 42) {
                rightAnswers++;
            }
        }
        if (rightAnswers == 0) {
            return new FailedOnCourseResultsSubmission(cmd.courseId, cmd.studentId);
        }
        if (solved) {
            return new CourseResultsSubmitted(cmd.courseId, cmd.studentId, rightAnswers * 20);
        } else {
            solved = true;
            return new CourseResultsSubmitted(cmd.courseId, cmd.studentId, rightAnswers * 20 + 20);
        }
    }

    @CommandHandler
    public Object on(@IdIn("courseId") StartFinalExam cmd) {
        return new FinalExamStarted(cmd.courseId, cmd.studentId, 3);
    }

    @CommandHandler
    public Object on(@IdIn("courseId") GraduateExam cmd) {
        int rightAnswers = 0;
        for (int[] answer : cmd.answers) {
            for (int value : answer) {
                if (value == 42) {
                    rightAnswers++;
                }
            }
        }
        return new FinalExamSubmitted(cmd.courseId, cmd.studentId, (rightAnswers * 20) / 3);
    }

    @EventListener
    public void on(Object e) {
    }
}
