package com.eventor.university;

import com.eventor.api.EventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;

@EventListener
public class StudentStat {
    Map<String, Set<String>> examsByStudent = new HashMap<String, Set<String>>();

    public Set<String> examsAttempted(String studentId) {
        if (examsByStudent.containsKey(studentId)) {
            return examsByStudent.get(studentId);
        } else {
            return emptySet();
        }
    }

    @EventListener
    public void on(ExamResultsSubmitted evt) {
        if (!examsByStudent.containsKey(evt.studentId)) {
            examsByStudent.put(evt.studentId, new HashSet<String>());
        }
        examsByStudent.get(evt.studentId).add(evt.examId);
    }
}
