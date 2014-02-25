package com.eventor.internal.reflection;

import com.eventor.api.annotations.*;
import com.eventor.internal.meta.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;

public class ClassProcessor {
    public Info apply(Iterable<Class<?>> classes) {
        HashSet<MetaAggregate> aggregates = new HashSet<MetaAggregate>();
        HashSet<MetaSubscriber> subscribers = new HashSet<MetaSubscriber>();
        HashSet<MetaSaga> sagas = new HashSet<MetaSaga>();
        for (Class<?> each : classes) {
            if (classAnnotated(each, Aggregate.class)) {
                aggregates.add(handleAggregate(each));
            } else if (classAnnotated(each, Saga.class)) {
                sagas.add(handleSaga(each));
            } else if (classAnnotated(each, EventListener.class)) {
                subscribers.add(handleSubscriber(each));
            }
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
                eventHandlers.add(new MetaHandler(getSingleParamType(each), null, false, false, null));
            }
        }
        return new MetaSubscriber(clazz, null, eventHandlers);
    }

    private MetaAggregate handleAggregate(Class<?> aggregateClass) {
        HashSet<MetaHandler> eventHandlers = new HashSet<MetaHandler>();
        HashSet<MetaHandler> commandHandlers = new HashSet<MetaHandler>();
        for (Method each : aggregateClass.getMethods()) {
            EventHandler eh = each.getAnnotation(EventHandler.class);
            if (eh != null) {
                eventHandlers.add(new MetaHandler(getSingleParamType(each), null, false,
                        each.getAnnotation(Start.class) != null, null));
            }
            CommandHandler ch = each.getAnnotation(CommandHandler.class);
            if (ch != null) {
                Class<?>[] params = each.getParameterTypes();
                if (params.length != 1) {
                    throw new RuntimeException("Only one param expected");
                }
                String idField = null;
                for (Annotation parameterAnnotation : each.getParameterAnnotations()[0]) {
                    if (parameterAnnotation.annotationType().equals(IdIn.class)) {
                        idField = ((IdIn) parameterAnnotation).value();
                    }
                }
                commandHandlers.add(new MetaHandler(getSingleParamType(each), null, false,
                        each.getAnnotation(Start.class) != null, idField));
            }
        }
        return new MetaAggregate(aggregateClass, commandHandlers, eventHandlers);
    }

    private MetaSaga handleSaga(Class<?> sagaClass) {
        HashSet<MetaHandler> eventHandlers = new HashSet<MetaHandler>();
        for (Method each : sagaClass.getMethods()) {
            EventHandler eh = each.getAnnotation(EventHandler.class);
            if (eh != null) {
                eventHandlers.add(new MetaHandler(getSingleParamType(each), null, false, false, null));
            }
        }
        return new MetaSaga(sagaClass, eventHandlers, eventHandlers);
    }

    private Class<?> getSingleParamType(Method each) {
        Class<?>[] parameterTypes = each.getParameterTypes();
        if (parameterTypes.length != 1) {
            throw new RuntimeException(String.format("Single argument method expected in place of %s", stringify(each)));

        }
        return parameterTypes[0];
    }

    private String stringify(Method method) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(method.getDeclaringClass().getSimpleName())
                .append(".")
                .append(method.getName())
                .append("(");
        for (Class<?> each : method.getParameterTypes()) {
            stringBuilder.append(each.getSimpleName()).append(", ");
        }
        return stringBuilder.append(")").toString();
    }

    private boolean classAnnotated(Class<?> candidate, Class<? extends Annotation> annotation) {
        return candidate.getAnnotation(annotation) != null;
    }
}
