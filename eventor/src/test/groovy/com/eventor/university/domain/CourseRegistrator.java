package com.eventor.university.domain;

import com.eventor.api.EventBus;
import com.eventor.university.api.CourseRegistered;

public class CourseRegistrator {
    public EventBus eventBus;

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void registerCourse(String courseId) {
        eventBus.publish(new CourseRegistered(courseId), null);
    }
}
