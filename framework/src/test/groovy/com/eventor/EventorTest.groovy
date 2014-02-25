package com.eventor

import com.eventor.api.CommandBus
import com.eventor.api.EventBus
import com.eventor.university.*
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import spock.lang.Specification


@RunWith(Sputnik)
class EventorTest extends Specification {
    def "Event should produce aggregates"() {
        setup:
        def instanceCreator = new SimpleInstanceCreator()
        def eventor = new Eventor([Course.class, CourseStat.class, Grades.class], instanceCreator)
        def eb = instanceCreator.getInstanceOf(EventBus.class)
        when:
        eb.publish(new CourseRegistered("Math-01"))
        Thread.sleep(500)
        then:
        instanceCreator.instancies.containsKey(Course.class)
    }

    def "Event listeners should receive messages from aggregates"() {
        setup:
        def instanceCreator = new SimpleInstanceCreator()
        def eventor = new Eventor([Course.class, CourseStat.class, Grades.class], instanceCreator)
        def eb = instanceCreator.getInstanceOf(EventBus.class)
        def cb = instanceCreator.getInstanceOf(CommandBus.class)
        when:
        eb.publish(new CourseRegistered("Math-01"))
        cb.submit(new SubmitResult("Math-01", "Bob", [1, 2, 42, 1, 3] as int[]))
        cb.submit(new SubmitResult("Math-01", "Poll", [42, 2, 42, 1, 3] as int[]))
        Thread.sleep(500)
        then:
        instanceCreator.getInstanceOf(CourseStat.class).bestResultForCourse("Math-01") == 40
    }

    def "Command handlers can handle commands and produce events directly"() {
        setup:
        def instanceCreator = new SimpleInstanceCreator()
        def eventor = new Eventor([Course.class, CourseStat.class, Grades.class], instanceCreator)
        def eb = instanceCreator.getInstanceOf(EventBus.class)
        def ch = instanceCreator.getInstanceOf(CourseRegistrator.class)
        ch.eventBus = eb
        when:
        ch.registerCourse("Algo-2013")
        Thread.sleep(500)
        then:
        instanceCreator.getInstanceOf(CourseStat.class).allCourses().contains("Algo-2013")
    }

    def "Aggregates maintain state between interactions"() {
        setup:
        def instanceCreator = new SimpleInstanceCreator()
        def eventor = new Eventor([Course.class, CourseStat.class, Grades.class], instanceCreator)
        def eb = instanceCreator.getInstanceOf(EventBus.class)
        def ch = instanceCreator.getInstanceOf(CourseRegistrator.class)
        ch.eventBus = eb
        def cb = instanceCreator.getInstanceOf(CommandBus.class)
        when:
        ch.registerCourse("Algo-2013")
        cb.submit(new SubmitResult("Algo-2013", "Bob", [42, 42, 42, 42, 42] as int[]))
        cb.submit(new SubmitResult("Algo-2013", "Poll", [42, 42, 42, 42, 42] as int[]))
        Thread.sleep(500)
        then:
        instanceCreator.getInstanceOf(Grades.class).grade("Algo-2013", "Bob") == 120 //extra bonus for first solution
        instanceCreator.getInstanceOf(Grades.class).grade("Algo-2013", "Poll") == 100
    }

    def "Aggregates have independent state"() {
        setup:
        def instanceCreator = new SimpleInstanceCreator()
        def eventor = new Eventor([Course.class, CourseStat.class, Grades.class], instanceCreator)
        def eb = instanceCreator.getInstanceOf(EventBus.class)
        def ch = instanceCreator.getInstanceOf(CourseRegistrator.class)
        ch.eventBus = eb
        def cb = instanceCreator.getInstanceOf(CommandBus.class)
        when:
        ch.registerCourse("Algo-2013")
        ch.registerCourse("Math-2013")
        cb.submit(new SubmitResult("Algo-2013", "Bob", [42, 42, 42, 42, 42] as int[]))
        cb.submit(new SubmitResult("Math-2013", "Poll", [42, 42, 42, 42, 42] as int[]))
        Thread.sleep(500)
        then:
        instanceCreator.getInstanceOf(Grades.class).grade("Algo-2013", "Bob") == 120 //extra bonus for first solution
        instanceCreator.getInstanceOf(Grades.class).grade("Math-2013", "Poll") == 120
    }

    def "Saga should handle event"() {
        setup:
        def courseId = "Algorithms"
        def instanceCreator = new SimpleInstanceCreator()
        def eventor = new Eventor([Course.class, CourseRegistration.class], instanceCreator)
        def eb = instanceCreator.getInstanceOf(EventBus.class)
        def ch = instanceCreator.getInstanceOf(CourseRegistrator.class)
        ch.eventBus = eb
        when:
        ch.registerCourse(courseId)
        Thread.sleep(500)
        then:
        instanceCreator.getInstanceOf(CourseRegistration.class).getCourseId() == courseId
    }
}
