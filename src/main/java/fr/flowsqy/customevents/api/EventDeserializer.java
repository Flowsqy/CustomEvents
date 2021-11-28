package fr.flowsqy.customevents.api;

import org.bukkit.configuration.ConfigurationSection;

import java.util.logging.Logger;

public interface EventDeserializer {

    Event deserialize(ConfigurationSection section, Logger logger);

}
