package fr.flowsqy.customevents.api;

import java.util.List;

public class EventData {

    private final String commandId;
    private final List<String> information;

    public EventData(String commandId, List<String> information) {
        this.commandId = commandId;
        this.information = information;
    }

    /**
     * Get the command id.
     * It will be used in-game to identify an event with the /customevents
     *
     * @return The command identifier
     */
    public String getCommandId() {
        return commandId;
    }

    /**
     * Get the information of this event.
     *
     * @return The event information
     */
    public List<String> getInfo() {
        return information;
    }

}
