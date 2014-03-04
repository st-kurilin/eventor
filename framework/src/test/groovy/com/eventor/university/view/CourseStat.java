package com.eventor.university.view;

import com.eventor.api.annotations.EventListener;
import com.eventor.university.api.CourseRegistered;
import com.eventor.university.api.CourseResultsSubmitted;

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
