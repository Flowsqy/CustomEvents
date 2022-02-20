package fr.flowsqy.customevents.api;

public class EventData {

    private final String commandId;

    public EventData(String commandId) {
        this.commandId = commandId;
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

}
