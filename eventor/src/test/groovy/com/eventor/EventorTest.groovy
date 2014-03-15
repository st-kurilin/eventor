package com.eventor

import com.eventor.university.api.CourseRegistered
import com.eventor.university.api.StartFinalExam
import com.eventor.university.api.SubmitAnswer
import com.eventor.university.api.SubmitPartAnswer
import com.eventor.university.domain.Course
import com.eventor.university.domain.CourseRegistrator
import com.eventor.university.domain.ExamGraduating
import com.eventor.university.view.CourseStat
import com.eventor.university.view.Grades
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
        cb.submit(new SubmitAnswer("Math-01", "Bob", [1, 2, 42, 1, 3] as int[]))
        cb.submit(new SubmitAnswer("Math-01", "Poll", [42, 2, 42, 1, 3] as int[]))
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
        Thread.sleep(100)
        cb.submit(new SubmitAnswer("Algo-2013", "Bob", [42, 42, 42, 42, 42] as int[]))
        cb.submit(new SubmitAnswer("Algo-2013", "Poll", [42, 42, 42, 42, 42] as int[]))
        Thread.sleep(500)
        then:
        instanceCreator.getInstanceOf(Grades).grade("Algo-2013", "Bob") == 120 //extra bonus for first solution
        instanceCreator.getInstanceOf(Grades).grade("Algo-2013", "Poll") == 100
    }

    def "Aggregates have independent state"() {
        when:
        ch.registerCourse("Algo-2013")
        ch.registerCourse("Math-2013")
        Thread.sleep(100)
        cb.submit(new SubmitAnswer("Algo-2013", "Bob", [42, 42, 42, 42, 42] as int[]))
        cb.submit(new SubmitAnswer("Math-2013", "Poll", [42, 42, 42, 42, 42] as int[]))
        Thread.sleep(500)
        then:
        instanceCreator.getInstanceOf(Grades).grade("Algo-2013", "Bob") == 120 //extra bonus for first solution
        instanceCreator.getInstanceOf(Grades).grade("Math-2013", "Poll") == 120
    }

    def "Sagas have independent state"() {
        when:
        ch.registerCourse("Algo-2014")
        ch.registerCourse("Math-2014")
        Thread.sleep(100)
        cb.submit(new StartFinalExam("Algo-2014", "Ann"))
        cb.submit(new StartFinalExam("Math-2014", "Marina"))
        Thread.sleep(100)
        cb.submit(new SubmitPartAnswer("Algo-2014", "Ann", [42, 0, 0, 0, 0] as int[], 1))
        cb.submit(new SubmitPartAnswer("Math-2014", "Marina", [42, 42, 42, 42, 42] as int[], 1))
        cb.submit(new SubmitPartAnswer("Algo-2014", "Ann", [42, 0, 0, 0, 0] as int[], 2))
        cb.submit(new SubmitPartAnswer("Math-2014", "Marina", [42, 42, 42, 42, 42] as int[], 2))
        cb.submit(new SubmitPartAnswer("Algo-2014", "Ann", [42, 0, 0, 0, 0] as int[], 3))
        cb.submit(new SubmitPartAnswer("Math-2014", "Marina", [42, 42, 42, 42, 42] as int[], 3))
        Thread.sleep(500)
        then:
        instanceCreator.getInstanceOf(Grades).grade("Algo-2014", "Ann") == 20
        instanceCreator.getInstanceOf(Grades).grade("Math-2014", "Marina") == 100
    }

    def "Saga should receive events and generate commands"() {
        when:
        ch.registerCourse("Bio-2014")
        Thread.sleep(100)
        cb.submit(new StartFinalExam("Bio-2014", "Alica"))
        Thread.sleep(100)
        cb.submit(new SubmitPartAnswer("Bio-2014", "Alica", [42, 42, 42, 42, 0] as int[], 1))
        cb.submit(new SubmitPartAnswer("Bio-2014", "Alica", [42, 42, 42, 42, 0] as int[], 2))
        cb.submit(new SubmitPartAnswer("Bio-2014", "Alica", [42, 42, 42, 42, 0] as int[], 3))
        Thread.sleep(500)
        then:
        instanceCreator.getInstanceOf(Grades).grade("Bio-2014", "Alica") == 80
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
