package com.eventor.internal.reflection

import com.eventor.university.Course
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import spock.lang.Specification


@RunWith(Sputnik)
class ClassProcessorTest extends Specification {
    def "ClassProcessorShould handle detect Aggregates"() {
        setup:
        def cp = new ClassProcessor()
        when:
        def info = cp.apply([Course.class])
        then:
        info.aggregates.size() == 1
        def descr = info.aggregates.iterator().next()
        descr.origClass == Course.class
        descr.eventHandlers.size() >= 1
        def eh = descr.eventHandlers.iterator().next()
        eh.expected == Object.class
    }
}
