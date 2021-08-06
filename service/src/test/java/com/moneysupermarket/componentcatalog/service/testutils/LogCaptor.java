package com.moneysupermarket.componentcatalog.service.testutils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LogCaptor implements AutoCloseable {

    private final ListAppender<ILoggingEvent> appender;
    private final List<ILoggingEvent> events;
    private final Logger logger;
    private final Level initialLevel;

    public LogCaptor(Class<?> clazz) {
        appender = new ListAppender<>();
        appender.start();
        // Collections.unmodifiableList() must be used instead of List.copyOf() as Collections.unmodifiableList()
        // provides a read-only wrapper around the source list but still allows that source list to be modified
        // and any changes are reflected in the read-only wrapper
        events = Collections.unmodifiableList(appender.list);
        logger = ((Logger) LoggerFactory.getLogger(clazz.getName()));
        initialLevel = logger.getLevel();
        logger.setLevel(Level.ALL);
        logger.addAppender(appender);
    }

    public List<ILoggingEvent> getEvents() {
        return events;
    }

    public List<SimplifiedLogEvent> getSimplifiedEvents() {
        return events.stream()
                .map(event -> new SimplifiedLogEvent(event.getLevel(), event.getFormattedMessage()))
                .collect(Collectors.toList());
    }

    @Override
    public void close() {
        logger.detachAppender(appender);
        logger.setLevel(initialLevel);
        appender.stop();
    }
}
