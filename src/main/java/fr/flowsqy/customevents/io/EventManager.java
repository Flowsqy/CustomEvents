package fr.flowsqy.customevents.io;

import fr.flowsqy.customevents.api.Event;
import fr.flowsqy.customevents.api.EventDeserializer;
import fr.flowsqy.customevents.event.EventChain;
import fr.flowsqy.customevents.event.EventDater;
import fr.flowsqy.customevents.event.EventQueue;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class EventManager {

    private Map<String, EventDeserializer> deserializers;
    private boolean register;
    private EventQueue eventQueue;

    public EventManager() {
        this.deserializers = new HashMap<>();
        this.register = true;
        this.eventQueue = null;
    }

    public EventDeserializer register(String type, EventDeserializer deserializer, boolean force) {
        if (!register) {
            throw new IllegalStateException("Can not register a deserializer after plugin loading");
        }
        if (force) {
            return deserializers.put(type, deserializer);
        }
        return deserializers.putIfAbsent(type, deserializer);
    }

    public void init(Logger logger, File eventFolder, Calendar now) {
        register = false;
        if (!eventFolder.canRead() || !eventFolder.isDirectory()) {
            logger.warning(eventFolder.getAbsolutePath() + " is not a readable directory");
        }
        final File[] files = eventFolder.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        final List<EventChain> chains = new ArrayList<>();
        for (File file : files) {
            final EventChain chain = initEvent(file, logger, now);
            if (chain != null) {
                chains.add(chain);
            }
        }
        if (!chains.isEmpty()) {
            eventQueue = new EventQueue(chains, now.getTimeInMillis());
        }
        deserializers = null;
    }

    private EventChain initEvent(File file, Logger logger, Calendar now) {
        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        final String eventType = configuration.getString("type");
        final String fileName = file.getName();
        if (eventType == null) {
            logger.warning(fileName + " try to load an event without type");
            return null;
        }
        final EventDeserializer deserializer = deserializers.get(eventType);
        if (deserializer == null) {
            logger.warning(fileName + " try to register an event that can not be deserialized (" + eventType + ")");
            return null;
        }
        final ConfigurationSection eventSection = configuration.getConfigurationSection("event");
        if (eventSection == null) {
            logger.warning(fileName + " does not comport 'event' section");
            return null;
        }
        final Event event = deserializer.deserialize(eventSection, logger);
        if (event == null) {
            return null;
        }
        final ConfigurationSection dateSection = configuration.getConfigurationSection("date");
        if (dateSection == null) {
            logger.warning(fileName + " doest not comport 'date' section");
            return null;
        }
        final EventDater dater = initDate(dateSection, logger, now);
        if (dater == null) {
            return null;
        }
        return new EventChain(event, dater);
    }

    private EventDater initDate(ConfigurationSection dateSection, Logger logger, Calendar now) {
        return null;
    }

}
