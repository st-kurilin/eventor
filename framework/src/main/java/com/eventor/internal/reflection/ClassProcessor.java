package com.eventor.internal.reflection;

import com.eventor.api.annotations.*;
import com.eventor.internal.meta.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.eventor.internal.EventorCollections.newSet;

public class ClassProcessor {
    public Info apply(Iterable<Class<?>> classes) {
        Set<MetaAggregate> aggregates = newSet();
        Set<MetaSubscriber> subscribers = newSet();
        Set<MetaSaga> sagas = newSet();
        Map<Class<? extends Annotation>, Iterable<Class<?>>> annotated =
                EventorReflections.getClassesAnnotated(classes, Aggregate.class, EventListener.class);
        for (Class<?> each : annotated.get(Aggregate.class)) {
            aggregates.add(handleAggregate(each));
        }
        for (Class<?> each : annotated.get(EventListener.class)) {
            subscribers.add(handleSubscriber(each));
        }
        return new Info(
                aggregates,
                sagas,
                subscribers
        );
    }

    private MetaSubscriber handleSubscriber(Class<?> clazz) {
        HashSet<MetaHandler> eventHandlers = new HashSet<MetaHandler>();
        for (Method each : clazz.getMethods()) {
            EventListener el = each.getAnnotation(EventListener.class);
            if (el != null) {
                eventHandlers.add(new MetaHandler(each, EventorReflections.getSingleParamType(each), null, false, false, null));
            }
        }
        return new MetaSubscriber(clazz, null, eventHandlers);
    }

    private MetaAggregate handleAggregate(Class<?> aggregateClass) {
        Map<Class<? extends Annotation>, Iterable<Method>> annotated =
                EventorReflections.getMethodsAnnotated(aggregateClass, EventHandler.class, CommandHandler.class);
        return new MetaAggregate(aggregateClass,
                extractAggregateCommandHandlers(annotated.get(CommandHandler.class)),
                extractAggregateEventHandlers(annotated.get(EventHandler.class)));
    }

    private Set<MetaHandler> extractAggregateEventHandlers(Iterable<Method> methods) {
        Set<MetaHandler> eventHandlers = newSet();
        for (Method each : methods) {
            eventHandlers.add(new MetaHandler(each, EventorReflections.getSingleParamType(each), null, false,
                    each.getAnnotation(Start.class) != null, null));
        }
        return eventHandlers;
    }

    private Set<MetaHandler> extractAggregateCommandHandlers(Iterable<Method> methods) {
        Set<MetaHandler> commandHandlers = newSet();
        for (Method each : methods) {
            Map<Class<? extends Annotation>, Annotation> annotations = EventorReflections.paramAnnotations(each, 0);
            String idField = annotations.containsKey(IdIn.class)
                    ? ((IdIn) annotations.get(IdIn.class)).value()
                    : null;
            commandHandlers.add(new MetaHandler(each, EventorReflections.getSingleParamType(each), null, false,
                    each.getAnnotation(Start.class) != null, idField));
        }
        return commandHandlers;
    }
}
