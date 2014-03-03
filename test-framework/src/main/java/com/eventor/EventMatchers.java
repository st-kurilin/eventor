package com.eventor;

import java.util.Set;

public class EventMatchers {
    public interface EventMatcher {
        void on(Set<Object> actualEvents, boolean andNoMoreFlagPresented);
    }

    //TODO: detalize error messages
    static class Contains implements EventMatcher {
        private final Set<Object> expected;

        Contains(Set<Object> expected) {
            this.expected = expected;
        }

        @Override
        public void on(Set<Object> actualEvents, boolean andNoMoreFlagPresented) {
            if (andNoMoreFlagPresented && actualEvents.size() != expected.size()) {
                throw new RuntimeException();
            }
            if (!actualEvents.containsAll(expected)) {
                throw new RuntimeException();
            }
        }
    }
}
