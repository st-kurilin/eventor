package com.eventor.university;

import com.eventor.api.Aggregate;
import com.eventor.api.CommandHandler;
import com.eventor.api.EventHandler;
import com.eventor.api.Start;

@Aggregate
public class Course {

    @EventHandler
    @Start
    public void on(ExamRegistered evt) {
    }

    @CommandHandler
    public Object on(SubmitResult cmd) {
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
            return new FailedOnExamResultsSubmition(cmd.courseId, cmd.courseId);
        }
        return new ExamResultsSubmitted(cmd.courseId, cmd.courseId, rightAnswers * 20);
    }

    @EventHandler
    public void on(Object e) {
    }
}
