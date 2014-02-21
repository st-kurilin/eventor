package com.eventor.api;

import java.util.HashMap;
import java.util.Map;

public class SimpleInstanceCreator implements InstanceCreator {
    public Map<Class<?>, Object> instancies = new HashMap<Class<?>, Object>();

    @Override
    public <T> T getInstanceOf(Class<T> clazz) {
        try {
            for (Class<?> each : instancies.keySet()) {
                if (clazz.isAssignableFrom(each)) {
                    return (T) instancies.get(each);
                }
            }
            T newInstance = clazz.newInstance();
            instancies.put(clazz, newInstance);
            return newInstance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
