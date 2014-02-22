package com.eventor.university;

import com.eventor.api.EventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;

@EventListener
public class StudentStat {
    Map<String, Set<String>> coursesByStudent = new HashMap<String, Set<String>>();

    public Set<String> coursesAttempted(String studentId) {
        if (coursesByStudent.containsKey(studentId)) {
            return coursesByStudent.get(studentId);
        } else {
            return emptySet();
        }
    }

    @EventListener
    public void on(CourseResultsSubmitted evt) {
        if (!coursesByStudent.containsKey(evt.studentId)) {
            coursesByStudent.put(evt.studentId, new HashSet<String>());
        }
        coursesByStudent.get(evt.studentId).add(evt.courseId);
    }
}
