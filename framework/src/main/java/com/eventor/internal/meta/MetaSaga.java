package com.eventor.internal.meta;

import java.util.Set;

public class MetaSaga extends MetaSubscriber {

    public MetaSaga(Class<?> origClass, Set<MetaHandler> commandHandlers, Set<MetaHandler> eventHandlers) {
        super(origClass, commandHandlers, eventHandlers);
    }

    public static class KeyLink {
        public final Class<?> class1;
        public final String field1;
        public final Class<?> class2;
        public final String field2;

        public KeyLink(Class<?> class1, String field1, Class<?> class2, String field2) {
            this.class1 = class1;
            this.field1 = field1;
            this.class2 = class2;
            this.field2 = field2;
        }
    }
}
