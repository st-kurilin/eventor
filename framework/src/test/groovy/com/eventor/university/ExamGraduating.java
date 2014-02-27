package com.eventor.university;

import com.eventor.api.Timeout;
import com.eventor.api.annotations.EventHandler;
import com.eventor.api.annotations.Finish;
import com.eventor.api.annotations.Saga;
import com.eventor.api.annotations.Start;

import java.util.concurrent.TimeUnit;

@Saga
public class ExamGraduating {
    @EventHandler
    @Start
    public Object on(CourseExamStarted evn) {
        return new Timeout(0, TimeUnit.HOURS,
                new SubmitResult(evn.courseId, evn.studentId, new int[]{0, 0, 0, 0, 0}));
    }

    @EventHandler
    public Object on(CourseResultsSubmitted evn) {
        return Finish.RESULT;
    }
}
