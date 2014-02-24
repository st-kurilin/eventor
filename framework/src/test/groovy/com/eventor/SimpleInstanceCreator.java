package com.eventor;

import com.eventor.api.InstanceCreator;

import java.util.HashMap;
import java.util.Map;

public class SimpleInstanceCreator implements InstanceCreator {
    public Map<Class<?>, Object> instancies = new HashMap<Class<?>, Object>();

    @Override
    public <T> T newInstanceOf(Class<T> clazz) {
        try {
            System.out.println("Create instance of " + clazz.getSimpleName());
            T newInstance = clazz.newInstance();
            instancies.put(clazz, newInstance);
            return newInstance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized <T> T getInstanceOf(Class<T> clazz) {
        for (Class<?> each : instancies.keySet()) {
            if (clazz.isAssignableFrom(each)) {
                return (T) instancies.get(each);
            }
        }
        return newInstanceOf(clazz);
    }

    @Override
    public synchronized <T> void putInstance(Class<T> clazz, T obj) {
        instancies.put(clazz, obj);
    }
}
