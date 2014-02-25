package com.eventor.internal;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class EventorCollections {
    public static Collection<?> toCollection(Object inp) {
        if (inp == null) return emptyList();
        if (inp instanceof Collection) return (Collection) inp;
        if (inp instanceof Iterable) {
            List<Object> result = EventorCollections.newList();
            for (Object each : (Iterable) inp) {
                result.add(each);
            }
            return result;
        }
        return singletonList(inp);
    }

    public static <K, V> Map<K, V> newMap() {
        return new HashMap<K, V>();
    }

    public static <T> List<T> newList() {
        return new ArrayList<T>();
    }

    public static <T> Set<T> newSet() {
        return new HashSet<T>();
    }
}
