package com.eventor.university;

import com.eventor.api.EventListener;

import java.util.HashMap;
import java.util.Map;

@EventListener
public class ExamStat {
    Map<String, Integer> results = new HashMap<String, Integer>();

    public int bestResult(String examId) {
        if (!results.containsKey(examId)) {
            return 0;
        }
        return results.get(examId);
    }

    @EventListener
    public void on(ExamResultsSubmitted evt) {
        if (!results.containsKey(evt.examId)) {
            results.put(evt.examId, 0);
        }
        results.put(evt.examId, Math.max(results.get(evt.examId), evt.result));
    }
}
