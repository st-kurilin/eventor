package com.eventor.api;

//todo: move to slf4j interface
public interface Log {
    void debug(String msg, Object... args);

    void info(String msg, Object... args);

    void warn(String msg, Object... args);
}
