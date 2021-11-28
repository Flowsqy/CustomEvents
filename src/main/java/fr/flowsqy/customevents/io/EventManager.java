package fr.flowsqy.customevents.io;

import fr.flowsqy.customevents.api.EventDeserializer;
import fr.flowsqy.customevents.event.EventQueue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class EventManager {

    private Map<String, EventDeserializer> events;
    private boolean register;
    private final EventQueue eventQueue;

    public EventManager() {
        this.events = new HashMap<>();
        this.register = true;
        this.eventQueue = null;
    }

    public EventDeserializer register(String event, EventDeserializer eventDeserializer, boolean force) {
        if (!register) {
            throw new IllegalStateException("Can not register a deserializer after plugin loading");
        }
        if (force) {
            return events.put(event, eventDeserializer);
        }
        return events.putIfAbsent(event, eventDeserializer);
    }

    public void init(Logger logger, File eventFolder) {
        register = false;
        if (!eventFolder.canRead() || !eventFolder.isDirectory()) {
            logger.warning(eventFolder.getAbsolutePath() + " is not a readable directory");
        }
        final File[] files = eventFolder.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            initEvent(file, logger);
        }
        events = null;
    }

    private void initEvent(File file, Logger logger) {

    }

}
