package com.eventor

import groovy.transform.EqualsAndHashCode
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

import static com.eventor.internal.EventorReflections.*

@RunWith(Sputnik)
public class EventorReflectionsSpec extends Specification {

    def "Should wrap value"() {
        expect:
        wrap(raw, wrapped.class) == wrapped
        where:
        wrapped << [new RedId('NY'), new RedId(42), new RedId(42L), UUID.fromString('550e8400-e29b-41d4-a716-44665544')]
        raw << ['NY', 42, 42L, '550e8400-e29b-41d4-a716-44665544']
    }

    def "Static method has more priority than constructor"() {
        expect:
        wrap('Hello', GreenId.class) == new GreenId('Hello from static')
    }

    def "Constructor should executed if valueOf has incompatible param or return type"() {
        expect:
        wrap(42, GreenId.class) == new GreenId(42)
    }

    def "Should throw exception if mark is invalid"() {
        when:
        validateMark(Object.class, Object.class, mark)
        then:
        thrown IllegalArgumentException
        where:
        mark << [' a', 'a ', 'a  b', '.a', 'a.', 'a..b']
    }

    def "Retrieve named value"() {
        expect:
        retrieveNamedValue(obj, mark) == val
        where:
        [mark, val, obj] << [
                ['id', 1, new Object() {
                    int id = 1
                }],
                ['myId', 2, new Object() {
                    public int getMyId() {
                        return 2
                    }
                }],
                ['foo.bar', 3, new Object() {
                    public Object getFoo() {
                        return new Object() {
                            int bar = 3
                        }
                    }
                }],
                ['aaa.bbb.ccc', 4, new Object() {
                    public Object getAaa() {
                        return new Object() {
                            public Object getBbb() {
                                return new Object() {
                                    int ccc = 4
                                }
                            }
                        }
                    }
                }]
        ]
    }
}

@EqualsAndHashCode
class RedId {
    int i
    long l
    String s

    public RedId(Integer i) {
        this.i = i
    }

    public RedId(Long l) {
        this.l = l
    }

    public RedId(String s) {
        this.s = s
    }
}

@EqualsAndHashCode
class GreenId {
    String s
    int i

    public GreenId(String s) {
        this.s = s
    }

    public GreenId(Integer i) {
        this.i = i
    }

    public static GreenId valueOf(String s) {
        return new GreenId(s + ' from static')
    }

    public static RedId valueOf(Integer s) {
        return new RedId(s)
    }
}
