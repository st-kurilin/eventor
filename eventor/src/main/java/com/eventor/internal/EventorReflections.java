package com.eventor.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import static com.eventor.internal.EventorCollections.newMap;
import static com.eventor.internal.EventorCollections.newSet;
import static com.eventor.internal.EventorPreconditions.assume;

public class EventorReflections {
    public static Map<Class<? extends Annotation>, Iterable<Class<?>>> getClassesAnnotated(
            Iterable<Class<?>> classes,
            Class<? extends Annotation>... annotations) {
        Map<Class<? extends Annotation>, Set<Class<?>>> result = newMap();
        for (Class<? extends Annotation> annotation : annotations) {
            result.put(annotation, EventorCollections.<Class<?>>newSet());
        }
        for (Class<?> clazz : classes) {
            for (Class<? extends Annotation> annotation : annotations) {
                if (classAnnotated(clazz, annotation)) {
                    result.get(annotation).add(clazz);
                }
            }
        }
        return (Map) result;
    }

    public static Map<Class<? extends Annotation>, Iterable<Method>> getMethodsAnnotated(
            Class<?> clazz, Class<? extends Annotation>... annotations) {
        Map<Class<? extends Annotation>, Set<Method>> result = newMap();
        for (Class<? extends Annotation> annotation : annotations) {
            result.put(annotation, EventorCollections.<Method>newSet());
        }
        for (Method method : clazz.getDeclaredMethods()) {
            for (Class<? extends Annotation> annotation : annotations) {
                if (method.getAnnotation(annotation) != null) {
                    result.get(annotation).add(method);
                }
            }
        }
        return (Map) result;
    }

    public static Class<?> getSingleParamType(Method each) {
        Class<?>[] parameterTypes = each.getParameterTypes();
        if (parameterTypes.length != 1) {
            throw new RuntimeException(
                    String.format("Single argument method expected in place of %s", stringify(each)));
        }
        return parameterTypes[0];
    }

    public static boolean classAnnotated(Class<?> candidate, Class<? extends Annotation> annotation) {
        return candidate.getAnnotation(annotation) != null;
    }

    public static Map<Class<? extends Annotation>, Annotation> paramAnnotations(Method method, int index) {
        Map<Class<? extends Annotation>, Annotation> result = EventorCollections.newMap();
        for (Annotation each : method.getParameterAnnotations()[index]) {
            if (result.containsKey(each.getClass())) {
                throw new RuntimeException("Repeatable annotations are not supported in " + stringify(method));
            }
            result.put(each.annotationType(), each);
        }
        return result;
    }

    public static Object retrieveAnnotatedValue(Object obj, Class<? extends Annotation> mark) {
        for (Field f : obj.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(mark)) {
                return getFieldValue(obj, f);
            }
        }
        throw new RuntimeException(String.format("Fail while getting %s from %s", mark, obj));
    }

    public static Class<?> retrieveTypeOfAnnotatedValue(Class<?> clazz, Class<? extends Annotation> mark) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(mark)) {
                if (!f.isAccessible()) f.setAccessible(true);
                return wrapPrimitive(f.getType());
            }
        }
        throw new RuntimeException(String.format("Fail while getting %s type from %s", mark, clazz));
    }

    public static Object retrieveNamedValue(Object obj, String mark) {
        String expectedGetterName = getterName(mark);
        for (Method m : obj.getClass().getDeclaredMethods()) {
            if (m.getName().equals(expectedGetterName)) {
                return getByGetter(obj, m);
            }
        }
        for (Field f : obj.getClass().getDeclaredFields()) {
            if (f.getName().equals(mark)) {
                return getFieldValue(obj, f);
            }
        }
        int dotIndex = mark.indexOf('.');
        if (dotIndex != -1) {
            String firstPart = mark.substring(0, dotIndex);
            String secondPart = mark.substring(dotIndex + 1);
            return retrieveNamedValue(retrieveNamedValue(obj, firstPart), secondPart);
        }
        throw new RuntimeException(String.format("Fail while getting %s from %s", mark, obj));
    }

    public static void validateMark(Class<?> classAnnotated, Class<?> classExpected, String mark) {
        // verify that last char does not have ".",
        // because "abc.".split("\\.") returns array that has only one "abc" item.
        if (mark.contains(" ") || mark.charAt(mark.length() - 1) == '.') {
            throw new IllegalArgumentException(String.format("Illegal IdIn specification [%s] in %s for %s",
                    mark, classAnnotated.getName(), classExpected.getName()));
        }
        for (String each : mark.split("\\.")) {
            if (each.isEmpty()) {
                throw new IllegalArgumentException(String.format("Illegal IdIn specification [%s] in %s for %s",
                        mark, classAnnotated.getName(), classExpected.getName()));
            }
        }
    }

    public static Collection<?> invoke(Object obj, Method m, Object... arg) {
        try {
            if (!m.isAccessible()) m.setAccessible(true);
            Object res = arg == null ? m.invoke(obj) : m.invoke(obj, arg);
            return EventorCollections.toCollection(res);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object newInstance(Object obj, Constructor c) {
        try {
            if (!c.isAccessible()) c.setAccessible(true);
            return c.newInstance(obj);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object getByGetter(Object obj, Method m) {
        try {
            assume(m.getParameterTypes().length == 0, "expect getter have no params");
            if (!m.isAccessible()) m.setAccessible(true);
            return m.invoke(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getterName(String mark) {
        return "get" + mark.substring(0, 1).toUpperCase() + mark.substring(1);
    }

    private static Object getFieldValue(Object obj, Field f) {
        try {
            if (!f.isAccessible()) f.setAccessible(true);
            return f.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static String stringify(Method method) {
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

    public static Iterable<Method> handlerMethods(Class<?> origClass, Class<?> paramClass) {
        Set<Method> res = newSet();
        for (Method each : origClass.getDeclaredMethods()) {
            if (each.getParameterTypes().length == 1 && each.getParameterTypes()[0].equals(paramClass)) {
                res.add(each);
            }
        }
        return res;
    }

    public static Object wrapId(Object id, Class<?> idClass) {
        for (Constructor c : idClass.getDeclaredConstructors()) {
            if (c.getGenericParameterTypes().length == 1 && wrapPrimitive(c.getParameterTypes()[0]).isAssignableFrom(id.getClass())) {
                return EventorReflections.newInstance(id, c);
            }
        }
        for (Method m : EventorReflections.handlerMethods(idClass, id.getClass())) {
            if (Modifier.isStatic(m.getModifiers())) {
                String name = m.getName();
                if (name.equals("valueOf") ||
                    name.equals("fromString") && m.getReturnType().equals(UUID.class)) {
                    return EventorReflections.invoke(null, m, id).toArray()[0];
                }
            }
        }
        throw new RuntimeException(String.format("Fail while converting '%s' id to '%s'", id, idClass));
    }

    private static Class<?> wrapPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() ? wrappers.get(clazz) : clazz;
    }

    public final static Map<Class<?>, Class<?>> wrappers = new HashMap<Class<?>, Class<?>>();
    static {
        wrappers.put(boolean.class, Boolean.class);
        wrappers.put(byte.class, Byte.class);
        wrappers.put(short.class, Short.class);
        wrappers.put(char.class, Character.class);
        wrappers.put(int.class, Integer.class);
        wrappers.put(long.class, Long.class);
        wrappers.put(float.class, Float.class);
        wrappers.put(double.class, Double.class);
    }
}
