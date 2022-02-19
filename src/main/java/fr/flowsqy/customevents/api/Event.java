package fr.flowsqy.customevents.api;

public interface Event {

    /**
     * The entry point of the event. It will be call when the event starts.
     */
    void perform();

    /**
     * Get the command id.
     * It will be used in-game to identify an event with the /customevents
     *
     * @return The command identifier
     */
    String getCommandId();

}
