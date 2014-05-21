package com.eventor

import com.eventor.api.annotations.Aggregate
import com.eventor.api.annotations.Id
import com.eventor.api.annotations.Start
import com.eventor.internal.ClassProcessor
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

@RunWith(Sputnik)
class ClassProcessorSpec extends Specification {

    def "ClassProcessor should not be able to handle aggregate that doesn't have Start annotation"() {
        when:
        new ClassProcessor().apply([Red])
        then:
        thrown RuntimeException
    }

    def "ClassProcessor should be able to handle aggregate that has Start annotation"() {
        when:
        def info = new ClassProcessor().apply([Green])
        then:
        info.aggregates.size() == 1
    }
}

@Aggregate
class Green {
    @Id
    String id

    @Start
    public void on(Object evt) {}
}

@Aggregate
class Red {
    @Id
    String id
}
