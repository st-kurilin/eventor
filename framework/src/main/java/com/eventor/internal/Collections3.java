package com.eventor.internal;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class Collections3 {
    public static Iterable<Object> toIterable(Object inp) {
        if (inp == null) {
            return emptyList();
        }
        return singletonList(inp);
    }
}
