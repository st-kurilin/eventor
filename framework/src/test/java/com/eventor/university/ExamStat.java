package com.eventor.university;

import com.eventor.api.EventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@EventListener
public class ExamStat {
    private Map<String, Integer> results = new HashMap<String, Integer>();


    public Set<String> allExams() {
        return results.keySet();
    }

    public int bestResultForExam(String courseId) {
        return results.get(courseId);
    }

    @EventListener
    public void on(ExamRegistered evt) {
        results.put(evt.courseId, 0);
    }

    @EventListener
    public void on(ExamResultsSubmitted evt) {
        results.put(evt.courseId, Math.max(results.get(evt.courseId), evt.result));
    }
}
