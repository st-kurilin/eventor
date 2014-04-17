package com.eventor;

import com.eventor.api.annotations.*;
import org.junit.Test;

import static com.eventor.internal.EventorPreconditions.assumeNotNull;

public class AggregateFixtureTest {
    @Test
    public void testContainsProduced() {
        AggregateFixture.toTest(Agg.class)
                .given()
                .event(new Created("foo"))
                .event(new Incremented("foo", 1))
                .event(new Incremented("foo", 2))
                .when()
                .command(new Increment("foo"))
                .command(new Increment("foo"))
                .than()
                .contains(new Incremented("foo", 4));
    }

    @Test(expected = AssertionError.class)
    public void testContainsProducedWrongExpectation() {
        AggregateFixture.toTest(Agg.class)
                .given()
                .event(new Created("foo"))
                .event(new Incremented("foo", 1))
                .event(new Incremented("foo", 2))
                .when()
                .command(new Increment("foo"))
                .command(new Increment("foo"))
                .than()
                .contains(new Incremented("foo", 5));
    }

    @Test
    public void testDoesntContains() {
        AggregateFixture.toTest(Agg.class)
                .given()
                .event(new Created("foo"))
                .event(new Incremented("foo", 1))
                .event(new Incremented("foo", 2))
                .when()
                .command(new Increment("foo"))
                .command(new Increment("foo"))
                .than()
                .doesntContain(new Incremented("foo", 6));
    }

    @Test(expected = AssertionError.class)
    public void testDoesntContainsWrong() {
        AggregateFixture.toTest(Agg.class)
                .given()
                .event(new Created("foo"))
                .event(new Incremented("foo", 1))
                .event(new Incremented("foo", 2))
                .when()
                .command(new Increment("foo"))
                .command(new Increment("foo"))
                .than()
                .doesntContain(new Incremented("foo", 4));
    }

    @Test
    public void testContainsAny() {
        AggregateFixture.toTest(Agg.class)
                .given()
                .event(new Created("foo"))
                .event(new Incremented("foo", 1))
                .event(new Incremented("foo", 2))
                .when()
                .command(new Increment("foo"))
                .command(new Increment("foo"))
                .than()
                .containsAnyOf(new Incremented("foo", 6), new Incremented("foo", 4));
    }

    @Test(expected = AssertionError.class)
    public void testContainsAnyWrong() {
        AggregateFixture.toTest(Agg.class)
                .given()
                .event(new Created("foo"))
                .event(new Incremented("foo", 1))
                .event(new Incremented("foo", 2))
                .when()
                .command(new Increment("foo"))
                .command(new Increment("foo"))
                .than()
                .containsAnyOf(new Incremented("foo", 6), new Incremented("foo", 7));
    }

    @Test
    public void testWhenEventsAreNotInThan() {
        AggregateFixture.toTest(Agg.class)
                .given()
                .event(new Created("foo"))
                .event(new Incremented("foo", 1))
                .event(new Incremented("foo", 2))
                .when()
                .command(new Increment("foo"))
                .command(new Increment("foo"))
                .than()
                .doesntContain(new Incremented("foo", 1));
    }


    @Aggregate
    private static class Agg {
        @Id
        String id;
        int counter;

        @EventListener
        @Start
        public void on(Created e) {
            id = e.id;
        }

        @CommandHandler
        public Object handle(@IdIn("id") Increment cmd) {
            return new Incremented(assumeNotNull(id, "Id should be setted"), counter + 1);
        }

        @EventListener
        public void on(Incremented e) {
            counter = e.current;
        }
    }

    private static class Created {
        String id;

        private Created(String id) {
            this.id = id;
        }
    }


    private static class Increment {
        String id;

        private Increment(String id) {
            this.id = id;
        }

    }

    private static class Incremented {
        String id;
        int current;

        private Incremented(String id, int current) {
            this.id = id;
            this.current = current;
        }

        @Override
        public String toString() {
            return "Incremented{" +
                    "id='" + id + '\'' +
                    ", current=" + current +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Incremented that = (Incremented) o;

            if (current != that.current) return false;
            if (id != null ? !id.equals(that.id) : that.id != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + current;
            return result;
        }
    }
}
