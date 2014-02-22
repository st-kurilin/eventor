package com.eventor.university;

import com.eventor.api.EventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@EventListener
public class CourseStat {
    private Map<String, Integer> results = new HashMap<String, Integer>();


    public Set<String> allCourses() {
        return results.keySet();
    }

    public int bestResultForCourse(String courseId) {
        return results.get(courseId);
    }

    @EventListener
    public void on(CourseRegistered evt) {
        results.put(evt.courseId, 0);
    }

    @EventListener
    public void on(CourseResultsSubmitted evt) {
        results.put(evt.courseId, Math.max(results.get(evt.courseId), evt.result));
    }
}
