package com.eventor.university;

import com.eventor.api.EventBus;

public class CourseRegistrator {
    public EventBus eventBus;

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void registerCourse(String courseId) {
        eventBus.publish(new CourseRegistered(courseId));
    }
}
