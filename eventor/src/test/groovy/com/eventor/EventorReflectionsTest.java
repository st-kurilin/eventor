package com.eventor;

import org.junit.Test;

import java.util.UUID;

import static com.eventor.internal.EventorReflections.retrieveNamedValue;
import static com.eventor.internal.EventorReflections.validateMark;
import static com.eventor.internal.EventorReflections.wrapId;
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

    @Test
    public void testWrapString() {
        assertWrappedIdEquals(new RedId("NY"), "NY", RedId.class);
    }

    @Test
    public void testWrapInteger() {
        assertWrappedIdEquals(new RedId(42), 42, RedId.class);
    }

    @Test
    public void testWrapLong() {
        assertWrappedIdEquals(new RedId((long) 42), (long) 42, RedId.class);
    }

    @Test
    public void testWrapUUID() {
        String name = "550e8400-e29b-41d4-a716-446655440000";
        assertWrappedIdEquals(UUID.fromString(name), name, GreenId.class);
    }

    private void assertIdEquals(int expected, String mark, Object obj) {
        assertEquals(expected, retrieveNamedValue(obj, mark));
    }

    private void assertWrappedIdEquals(Object expected, Object id, Class<?> idClass) {
        assertEquals(expected, wrapId(id, idClass));
    }
}

class RedId {
    Integer i;
    Long l;
    String s;

    public RedId(int i) {
        this.i = i;
    }

    public RedId(long l) {
        this.l = l;
    }

    public RedId(String s) {
        this.s = s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RedId redId = (RedId) o;

        if (i != null ? !i.equals(redId.i) : redId.i != null) return false;
        if (l != null ? !l.equals(redId.l) : redId.l != null) return false;
        if (s != null ? !s.equals(redId.s) : redId.s != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = i != null ? i.hashCode() : 0;
        result = 31 * result + (l != null ? l.hashCode() : 0);
        result = 31 * result + (s != null ? s.hashCode() : 0);
        return result;
    }
}

class GreenId {
    UUID uuid;

    public GreenId(UUID uuid) {
        this.uuid = uuid;
    }

    public static UUID fromString(String s) {
        return UUID.fromString(s);
    }
}