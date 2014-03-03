package com.eventor.university;

import com.eventor.api.annotations.*;

import java.util.HashMap;
import java.util.Map;

@Saga
public class ExamGraduating {
    @Id
    private String id;
    private Map<Integer, int[]> submission = new HashMap<Integer, int[]>();
    private Integer parts;

    @EventListener
    @Start
    public Object on(FinalExamStarted evt) {
        id = evt.courseId;
        parts = evt.parts;
        return null;
    }

    @CommandHandler
    public Object on(@IdIn("courseId") SubmitPartAnswer cmd) {
        if (cmd.part <= 0 || cmd.part > parts) throw new IllegalArgumentException(cmd.part + " part doesn't exist at " + id + " exam");
        if (submission.keySet().contains(cmd.part)) throw new IllegalStateException(cmd.part + " part is already submitted at " + id + " exam");
        submission.put(cmd.part, cmd.answer);
        if (submission.size() == parts) {
            int[][] answers = new int[parts][];
            for (int i = 0; i < answers.length; i++) {
                answers[i] = submission.get(i + 1);
            }
            return new GraduateExam(cmd.courseId, cmd.studentId, answers);
        } else {
            return null;
        }
    }
}
