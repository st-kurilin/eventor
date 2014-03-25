package com.eventor.impl;

import com.eventor.api.InstanceCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SimpleInstanceCreator implements InstanceCreator {
    public Map<Class<?>, Object> instancies = new HashMap<Class<?>, Object>();
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public synchronized <T> T findInstanceOf(Class<T> clazz) {
        Object result = findInstance(clazz);
        if (result == null) {
            throw new IllegalStateException("Could not find instance for " + clazz);
        }
        return (T) result;
    }

    private <T> T findInstance(Class<T> clazz) {
        for (Class<?> each : instancies.keySet()) {
            if (clazz.isAssignableFrom(each)) {
                return (T) instancies.get(each);
            }
        }
        return null;
    }

    @Override
    public <T> T findOrCreateInstanceOf(Class<T> clazz, boolean isExpectedToBeSingleton) {
        Object result = findInstance(clazz);
        if (result == null) {
            result = createInstance(clazz);
            if (isExpectedToBeSingleton) {
                instancies.put(clazz, result);
            }
        }
        return (T) result;

    }

    private <T> T createInstance(Class<T> clazz) {
        try {
            log.info("Creating instance for class {} ({})", clazz.getSimpleName(), clazz.getName());
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
