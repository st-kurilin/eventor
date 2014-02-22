package com.eventor.university;

import com.eventor.api.EventBus;

public class ExamRegistrator {
    public EventBus eventBus;

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void registerExam(String examId) {
        eventBus.publish(new ExamRegistered(examId));
    }
}
