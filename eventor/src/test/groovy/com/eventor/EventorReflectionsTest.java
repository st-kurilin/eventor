package com.eventor;

import org.junit.Test;

import static com.eventor.internal.EventorReflections.retrieveNamedValue;
import static com.eventor.internal.EventorReflections.validateMark;
import static org.junit.Assert.assertEquals;

public class EventorReflectionsTest {
    @Test
    public void testExtractIdByField() {
        assertIdEquals(1, "id", new Object() {
            int id = 1;
        });
    }

    @Test
    public void testExtractIdByGetter() {
        assertIdEquals(2, "id", new Object() {
            public int getId() {
                return 2;
            }
        });
    }

    @Test
    public void testExtractIdByObject() {
        assertIdEquals(3, "foo.bar", new Object() {
            public Object getFoo() {
                return new Object() {
                    int bar = 3;
                };
            }
        });
    }

    @Test
    public void testExtractIdByObjects() {
        assertIdEquals(4, "aaa.bbb.ccc", new Object() {
            public Object getAaa() {
                return new Object() {
                    public Object getBbb() {
                        return new Object() {
                            int ccc = 4;
                        };
                    }
                };
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenMarkHasSpace() {
        validateMark(Object.class, Object.class, "a ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenMarkStartsWithDot() {
        validateMark(Object.class, Object.class, ".a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenMarkEndsWithDot() {
        validateMark(Object.class, Object.class, "b.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenMarkHasMoreThenOneDot() {
        validateMark(Object.class, Object.class, "a..b");
    }

    private void assertIdEquals(int expected, String mark, Object obj) {
        assertEquals(expected, retrieveNamedValue(obj, mark));
    }
}
