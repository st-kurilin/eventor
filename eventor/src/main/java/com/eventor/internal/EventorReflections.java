package com.eventor.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

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

    public static Class<?> retrieveTypeOfAnnotatedValue(Class<?> clazz, Class<? extends Annotation> annotation) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(annotation)) {
                if (!f.isAccessible()) f.setAccessible(true);
                return boxPrimitiveType(f.getType());
            }
        }
        throw new RuntimeException(String.format("Fail while getting %s type from %s", annotation, clazz));
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

    private static Object newInstance(Constructor c, Object... arg) {
        try {
            if (!c.isAccessible()) c.setAccessible(true);
            return c.newInstance(arg);
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
            if (checkMethodParam(each, paramClass)) {
                res.add(each);
            }
        }
        return res;
    }

    public static Object wrap(Object obj, Class<?> wrapper) {
        if (obj == null) return null;
        if (obj.getClass().isAssignableFrom(wrapper)) {
            return obj;
        } else {
            for (Method m : handlerMethods(wrapper, obj.getClass())) {
                if (m.getReturnType().isAssignableFrom(wrapper) && Modifier.isStatic(m.getModifiers())) {
                    String name = m.getName();
                    if (name.equals("valueOf") || name.equals("fromString")) {
                        Object[] res = invoke(null, m, obj).toArray();
                        if (res.length != 1) {
                            throw new RuntimeException(String.format(
                                    "Fail while wrapping '%s' to '%s' via static '%s' method", obj, wrapper, name));
                        }
                        return res[0];
                    }
                }
            }
            for (Constructor c : wrapper.getDeclaredConstructors()) {
                if (checkConstructorParam(c, obj.getClass())) {
                    return newInstance(c, obj);
                }
            }
            throw new RuntimeException(String.format("Fail while wrapping '%s' to '%s'", obj, wrapper));
        }
    }

    private static boolean checkMethodParam(Method m, Class<?> paramClass) {
        return m.getParameterTypes().length == 1 && boxPrimitiveType(m.getParameterTypes()[0]).equals(paramClass);
    }

    private static boolean checkConstructorParam(Constructor c, Class<?> paramClass) {
        return c.getParameterTypes().length == 1 &&
                boxPrimitiveType(c.getParameterTypes()[0]).isAssignableFrom(paramClass);
    }

    private static Class<?> boxPrimitiveType(Class<?> clazz) {
        return primitiveTypes.containsKey(clazz) ? primitiveTypes.get(clazz) : clazz;
    }

    private static Map<Class<?>, Class<?>> primitiveTypes = newMap();

    static {
        primitiveTypes.put(byte.class, Byte.class);
        primitiveTypes.put(short.class, Short.class);
        primitiveTypes.put(int.class, Integer.class);
        primitiveTypes.put(long.class, Long.class);
    }
}
