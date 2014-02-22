package com.eventor.university;

import com.eventor.api.EventBus;

public class ExamRegistrator {
    public EventBus eventBus;

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void registerExam(String courseId) {
        eventBus.publish(new ExamRegistered(courseId));
    }
}
