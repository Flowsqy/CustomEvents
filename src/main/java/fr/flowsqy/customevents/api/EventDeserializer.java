package fr.flowsqy.customevents.api;

import org.bukkit.configuration.ConfigurationSection;

import java.util.logging.Logger;

public interface EventDeserializer {

    /**
     * Deserialize the event configuration
     *
     * @param section The {@link ConfigurationSection} that contains event parameters
     * @param logger  The logger to register warning if the configuration is wrong filled
     * @return An {@link Event} with the parameters specified in the configuration
     */
    Event deserialize(ConfigurationSection section, Logger logger);

}
