# CustomEvents

A Bukkit plugin which offers an API to register custom events that will be launched at a specific date.

## Configuration

To create an event, add a yaml file in the 'event' directory that respect the template bellow. You can have as many
event as you want.

```yaml
# The date of the week when this event must be fired. There is no limit for the number of dates
date:
  1: # This section's name does not matter, it just needs to be unique.
    day: 1 # Between 1 and 7. 1 = SUNDAY ; 2 = MONDAY ; ... ; 7 = SATURDAY
    hour: 18 # Between 0 and 23
    minute: 0 # Between 0 and 59
  # Sunday at 18:00

  2:
    day: 2
    hour: 20
    minute: 30
  # Monday at 20:30

# The bellow keys are specific to each event, 
# so you need to check the documentation of the event plugin

# The type of the event
type: <type>

# The event parameters
event: <event>
```

## Developers

How to include the API with Maven:

```xml

<project>
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    <dependencies>
        <dependency>
            <groupId>com.github.Flowsqy</groupId>
            <artifactId>CustomEvents</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</project>
```

You can register a custom event like this:

```java
package fr.flowsqy.customeventexample;

import fr.flowsqy.customevents.CustomEventsPlugin;
import fr.flowsqy.customevents.api.Event;
import fr.flowsqy.customevents.api.EventDeserializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class CustomEventExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("CustomEvents");
        if (plugin instanceof CustomEventsPlugin customEventsPlugin) {
            customEventsPlugin.getEventManager().register(
                    "example", // That is the type needed in the configuration (see above)
                    new ExampleEventDeserializer(),
                    false // Whether this event can replace an existing event with the same identifier
            );
        }
    }

    // The deserializer class. Can be in a separate file
    private static final class ExampleEventDeserializer implements EventDeserializer {

        @Override
        public Event deserialize(ConfigurationSection section, Logger logger, String fileName) {
            final String message = section.getString("message");
            if (message == null) {
                logger.warning("The message key is null in " + fileName + ", you need to set it. ");
                return null;
            }
            return new ExampleEvent(message);
        }
    }

    // The event class. Can be in a separate file
    private static final class ExampleEvent implements Event {

        private final String message;

        public ExampleEvent(String message) {
            this.message = message;
        }

        @Override
        public void perform() {
            Bukkit.broadcastMessage(message);
        }
    }

}
```

It will text a message specified in the configuration each time the event is fired.

## Building

Just clone the repository and do `mvn clean install` or `mvn clean package`. The jar is in the _target_ directory.