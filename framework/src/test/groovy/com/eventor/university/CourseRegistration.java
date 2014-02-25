package com.eventor.university;

import com.eventor.api.Timeout;

import com.eventor.api.annotations.EventHandler;
import com.eventor.api.annotations.Saga;

import java.util.concurrent.TimeUnit;

@Saga
public class CourseRegistration {
    CourseRegistered courseRegistered;

    public String getCourseId(){
        return  courseRegistered == null ? null : courseRegistered.courseId;
    }

    @EventHandler
    public Timeout on(CourseRegistered evn) {
        this.courseRegistered = evn;
        return new Timeout(2, TimeUnit.HOURS, new RegistrationTimeout());
    }

    private static class RegistrationTimeout{
    }
}