package com.eventor.api

import com.eventor.university.*
import spock.lang.Specification

class EventorTest extends Specification {
    def "Event should produce aggregates"() {
        setup:
        def instanceCreator = new SimpleInstanceCreator()
        def eventor = new Eventor([Exam.class, ExamStat.class, StudentStat.class], instanceCreator)
        def eb = instanceCreator.getInstanceOf(EventBus.class)
        when:
        eb.publish(new ExamRegistered("Math-01"));
        then:
        instanceCreator.instancies.containsKey(Exam.class)
    }

    def "Event listeners should receive messages from aggregates"() {
        setup:
        def instanceCreator = new SimpleInstanceCreator()
        def eventor = new Eventor([Exam.class, ExamStat.class, StudentStat.class], instanceCreator)
        def eb = instanceCreator.getInstanceOf(EventBus.class)
        when:
        eb.publish(new ExamRegistered("Math-01"));
        eb.publish(new SubmitResult("Math-01", "Bob", [1, 2, 42, 1, 3] as int[]));
        eb.publish(new SubmitResult("Math-01", "Poll", [42, 2, 42, 1, 3] as int[]));
        then:
        instanceCreator.instancies.get(ExamStat.class).bestResult("Math-01") == 40
    }
}
