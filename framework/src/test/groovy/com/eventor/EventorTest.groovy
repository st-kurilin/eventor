package com.eventor

import com.eventor.university.*
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

@RunWith(Sputnik)
class EventorTest extends Specification {
    def "Event should produce aggregates"() {
        when:
        eb.publish(new CourseRegistered("Math-01"))
        Thread.sleep(500)
        then:
        instanceCreator.instancies.containsKey(Course)
    }

    def "Event listeners should receive messages from aggregates"() {
        when:
        eb.publish(new CourseRegistered("Math-01"))
        cb.submit(new SubmitResult("Math-01", "Bob", [1, 2, 42, 1, 3] as int[]))
        cb.submit(new SubmitResult("Math-01", "Poll", [42, 2, 42, 1, 3] as int[]))
        Thread.sleep(500)
        then:
        instanceCreator.getInstanceOf(CourseStat).bestResultForCourse("Math-01") == 40
    }

    def "Command handlers can handle commands and produce events directly"() {
        when:
        ch.registerCourse("Algo-2013")
        Thread.sleep(500)
        then:
        instanceCreator.getInstanceOf(CourseStat).allCourses().contains("Algo-2013")
    }

    def "Aggregates maintain state between interactions"() {
        when:
        ch.registerCourse("Algo-2013")
        cb.submit(new SubmitResult("Algo-2013", "Bob", [42, 42, 42, 42, 42] as int[]))
        cb.submit(new SubmitResult("Algo-2013", "Poll", [42, 42, 42, 42, 42] as int[]))
        Thread.sleep(1000)
        then:
        instanceCreator.getInstanceOf(Grades).grade("Algo-2013", "Bob") == 120 //extra bonus for first solution
        instanceCreator.getInstanceOf(Grades).grade("Algo-2013", "Poll") == 100
    }

    def "Aggregates have independent state"() {
        when:
        ch.registerCourse("Algo-2013")
        ch.registerCourse("Math-2013")
        cb.submit(new SubmitResult("Algo-2013", "Bob", [42, 42, 42, 42, 42] as int[]))
        cb.submit(new SubmitResult("Math-2013", "Poll", [42, 42, 42, 42, 42] as int[]))
        Thread.sleep(1000)
        then:
        instanceCreator.getInstanceOf(Grades).grade("Algo-2013", "Bob") == 120 //extra bonus for first solution
        instanceCreator.getInstanceOf(Grades).grade("Math-2013", "Poll") == 120
    }

    def "Saga has to raise event by timeout"() {
        when:
        eb.publish(new CourseRegistered("Algorithms"))
        cb.submit(new StartCourseExam("Algorithms", "Alica"))
        Thread.sleep(3000)
        then:
        instanceCreator.getInstanceOf(Grades).grade("Algorithms", "Alica") == -1 // specific score due to timeout
    }

    def instanceCreator = new SimpleInstanceCreator()
    def eventor = new Eventor([ExamGraduating, Course, CourseStat, Grades], instanceCreator)
    def eb = eventor.getEventBus()
    def cb = eventor
    def ch = instanceCreator.getInstanceOf(CourseRegistrator)

    def setup() {
        ch.eventBus = eb
    }
}
