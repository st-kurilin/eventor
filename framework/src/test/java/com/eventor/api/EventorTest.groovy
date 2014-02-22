package com.eventor.api

import com.eventor.university.*
import spock.lang.Specification

class EventorTest extends Specification {
    def "Event should produce aggregates"() {
        setup:
        def instanceCreator = new SimpleInstanceCreator()
        def eventor = new Eventor([Course.class, CourseStat.class, StudentStat.class], instanceCreator)
        def eb = instanceCreator.getInstanceOf(EventBus.class)
        when:
        eb.publish(new CourseRegistered("Math-01"));
        then:
        instanceCreator.instancies.containsKey(Course.class)
    }

    def "Event listeners should receive messages from aggregates"() {
        setup:
        def instanceCreator = new SimpleInstanceCreator()
        def eventor = new Eventor([Course.class, CourseStat.class, StudentStat.class], instanceCreator)
        def eb = instanceCreator.getInstanceOf(EventBus.class)
        when:
        eb.publish(new CourseRegistered("Math-01"));
        eb.publish(new SubmitResult("Math-01", "Bob", [1, 2, 42, 1, 3] as int[]));
        eb.publish(new SubmitResult("Math-01", "Poll", [42, 2, 42, 1, 3] as int[]));
        then:
        instanceCreator.instancies.get(CourseStat.class).bestResultForCourse("Math-01") == 40
    }

    def "Command handlers can handle commands and produce events directly"() {
        setup:
        def instanceCreator = new SimpleInstanceCreator()
        def eventor = new Eventor([Course.class, CourseStat.class, StudentStat.class], instanceCreator)
        def eb = instanceCreator.getInstanceOf(EventBus.class)
        def ch = instanceCreator.getInstanceOf(CourseRegistrator.class)
        ch.eventBus = eb
        when:
        ch.registerCourse("Algo-2013")
        then:
        instanceCreator.instancies.get(CourseStat.class).allCourses().contains("Algo-2013")
    }
}
