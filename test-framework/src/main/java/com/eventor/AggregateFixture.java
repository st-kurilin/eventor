package com.eventor;

import com.eventor.api.AggregateRepository;
import com.eventor.api.InstanceCreator;
import com.eventor.impl.SimpleInstanceCreator;
import com.eventor.internal.EventorReflections;
import com.eventor.internal.EventorSleeper;

import java.util.Set;
import java.util.concurrent.Callable;

import static com.eventor.internal.EventorCollections.newSet;
import static com.eventor.internal.EventorPreconditions.assume;
import static com.eventor.internal.EventorPreconditions.assumeNotNull;

public class AggregateFixture<T> {
    private T aggregate;
    private Object aggregateId;
    private Class<T> aggregateClass;
    private Callable<? extends T> aggregateProvider;
    private Eventor eventor;
    private InstanceCreator instanceCreator;
    private Set<Object> givenEvents = newSet();

    private AggregateFixture(Class<T> aggregateClass, Callable<? extends T> aggregateProvider) {
        this.aggregateClass = aggregateClass;
        this.aggregateProvider = aggregateProvider;
    }

    public static <T, O extends T> AggregateFixture<T> toTest(final O aggregate) {
        return new AggregateFixture<T>((Class<T>) aggregate.getClass(), new Callable<T>() {
            @Override
            public T call() throws Exception {
                return aggregate;
            }
        });
    }

    public static <T> AggregateFixture<T> toTest(final Class<T> aggregateClass) {
        return new AggregateFixture<T>(aggregateClass, new Callable<T>() {
            @Override
            public T call() throws Exception {
                return EventorReflections.newInstance(aggregateClass);
            }
        });
    }


    public static <T> AggregateFixture<T> toTest(Class<T> aggregateClass, Callable<T> aggregateProvider) {
        return new AggregateFixture<T>(aggregateClass, aggregateProvider);
    }

    public Given given() {
        instanceCreator = newInstanceCreator();
        eventor = new EventorBuilder()
                //.eventSourcedAggregateRepository()
                .addClasses(aggregateClass, EventsRecorder.class)
                .withInstanceCreator(instanceCreator)
                .withAggregateRepository(new AggregateRepository() {
                    @Override
                    public <T> T getById(Class<T> clazz, Object id) {
                        assume(id.equals(aggregateId), "Ids expected to equal: [%s], [%s]", id, aggregateId);
                        assumeNotNull(aggregate, "Aggregate was no created");
                        return (T) aggregate;
                    }

                    @Override
                    public void save(Object id, Object aggregate) {
                        if (aggregateId != null) assume(aggregateId.equals(id), "Could not persist two aggregates");
                        //assume(AggregateFixture.this.aggregate == aggregate, "");
                        aggregateId = assumeNotNull(id, "");
                    }
                })
                .build();
        return new Given();
    }

    private InstanceCreator newInstanceCreator() {
        return new InstanceCreator() {
            InstanceCreator backup = new SimpleInstanceCreator();

            @Override
            public <T> T findInstanceOf(Class<T> clazz) {
                if (!clazz.isAssignableFrom(aggregateClass)) {
                    return backup.findInstanceOf(clazz);
                }
                return (T) aggregate;
            }

            @Override
            public <T> T findOrCreateInstanceOf(Class<T> clazz, boolean isExpectedToBeSingleton) {
                try {
                    if (!clazz.isAssignableFrom(aggregateClass)) {
                        return backup.findOrCreateInstanceOf(clazz, isExpectedToBeSingleton);
                    }
                    aggregate = aggregateProvider.call();
                    return (T) aggregate;
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public class Given {
        public Given event(Object event) {
            eventor.getEventBus().publish(event, aggregateId);
            givenEvents.add(event);
            EventorSleeper.sleep(1000);
            return this;
        }

        public Given events(Object... events) {
            for (Object each : events) event(each);
            return this;
        }


        public When when() {
            return new When();
        }
    }

    public class When {
        public When command(Object cmd) {
            eventor.submit(cmd);
            return this;
        }

        public When commands(Object... cmds) {
            for (Object each : cmds) command(each);
            return this;
        }

        public RecordedEventsValidator than() {
            EventorSleeper.sleep(500);
            EventsRecorder recorder = instanceCreator.findOrCreateInstanceOf(EventsRecorder.class, true);
            Set<Object> whenEvents = newSet();
            for (Object each : recorder.getAllEvetns()) {
                if (!givenEvents.contains(each)) {
                    whenEvents.add(each);
                }
            }
            return new RecordedEventsValidator(whenEvents);
        }
    }

}
