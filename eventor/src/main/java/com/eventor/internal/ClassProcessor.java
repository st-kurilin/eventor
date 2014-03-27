package com.eventor.internal;

import com.eventor.api.annotations.*;
import com.eventor.internal.meta.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static com.eventor.internal.EventorCollections.concat;
import static com.eventor.internal.EventorCollections.newSet;
import static com.eventor.internal.EventorReflections.getClassesAnnotated;

public class ClassProcessor {
    public Info apply(Iterable<Class<?>> classes) {
        Set<MetaAggregate> aggregates = newSet();
        Set<MetaSubscriber> subscribers = newSet();
        Set<MetaSaga> sagas = newSet();
        Map<Class<? extends Annotation>, Iterable<Class<?>>> annotated =
                getClassesAnnotated(classes, CommandHandler.class, Aggregate.class, EventListener.class, Saga.class);
        for (Class<?> each : annotated.get(Aggregate.class)) {
            aggregates.add(handleAggregate(each));
        }
        for (Class<?> each : annotated.get(Saga.class)) {
            sagas.add(handleSaga(each));
        }
        for (Class<?> each : concat(annotated.get(EventListener.class), annotated.get(CommandHandler.class))) {
            subscribers.add(handleSubscriber(each));
        }
        return new Info(
                aggregates,
                sagas,
                subscribers
        );
    }

    private MetaSubscriber handleSubscriber(Class<?> clazz) {
        Set<MetaHandler> handlers = newSet();
        Set<MetaHandler> listener = newSet();
        for (Method each : clazz.getMethods()) {
            EventListener el = each.getAnnotation(EventListener.class);
            CommandHandler ch = each.getAnnotation(CommandHandler.class);
            EventorPreconditions.assume(ch == null || el == null,
                    "Same method could not handle commands and events [%s.%s]", clazz, each);

            if (el != null) {
                listener.add(new MetaHandler(each, EventorReflections.getSingleParamType(each),
                        null, false, false, null));
            }
            if (ch != null) {
                handlers.add(new MetaHandler(each, EventorReflections.getSingleParamType(each),
                        null, false, false, null));
            }
        }
        return new MetaSubscriber(clazz, handlers, listener);
    }

    private MetaAggregate handleAggregate(Class<?> aggregateClass) {
        Map<Class<? extends Annotation>, Iterable<Method>> annotated =
                EventorReflections.getMethodsAnnotated(aggregateClass, EventHandler.class,
                        CommandHandler.class, EventListener.class);
        return new MetaAggregate(aggregateClass,
                extactIdType(aggregateClass),
                extractCommandHandlers(aggregateClass, annotated.get(CommandHandler.class)),
                extractEventHandlers(
                        concat(annotated.get(EventHandler.class), annotated.get(EventListener.class))));
    }

    private MetaSaga handleSaga(Class<?> sagaClass) {
        Map<Class<? extends Annotation>, Iterable<Method>> annotated =
                EventorReflections.getMethodsAnnotated(sagaClass, EventListener.class, CommandHandler.class);
        return new MetaSaga(sagaClass,
                extactIdType(sagaClass),
                extractCommandHandlers(sagaClass, annotated.get(CommandHandler.class)),
                extractEventHandlers(annotated.get(EventListener.class)));
    }

    private Set<MetaHandler> extractEventHandlers(Iterable<Method> methods) {
        Set<MetaHandler> eventHandlers = newSet();
        for (Method each : methods) {
            eventHandlers.add(new MetaHandler(each, EventorReflections.getSingleParamType(each), null, false,
                    each.getAnnotation(Start.class) != null, null));
        }
        return eventHandlers;
    }

    private Set<MetaHandler> extractCommandHandlers(Class<?> aggregateClass, Iterable<Method> methods) {
        Set<MetaHandler> commandHandlers = newSet();
        for (Method each : methods) {
            Map<Class<? extends Annotation>, Annotation> annotations = EventorReflections.paramAnnotations(each, 0);
            String idField = null;
            Class<?> expected = EventorReflections.getSingleParamType(each);
            if (annotations.containsKey(IdIn.class)) {
                idField = ((IdIn) annotations.get(IdIn.class)).value();
                EventorReflections.validateMark(aggregateClass, expected, idField);
            }
            commandHandlers.add(new MetaHandler(each, expected, null, false,
                    each.getAnnotation(Start.class) != null, idField));
        }
        return commandHandlers;
    }

    private Class<?> extactIdType(Class<?> origClass) {
        return EventorReflections.retrieveTypeOfAnnotatedValue(origClass, Id.class);
    }
}
