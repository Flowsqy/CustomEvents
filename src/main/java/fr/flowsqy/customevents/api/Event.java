package fr.flowsqy.customevents.api;

public interface Event {

    /**
     * The entry point of the event. It will be call when the event starts.
     */
    void perform();

    /**
     * Get the {@link EventData}
     *
     * @return The {@link EventData} of this {@link Event}
     */
    EventData getData();

}
