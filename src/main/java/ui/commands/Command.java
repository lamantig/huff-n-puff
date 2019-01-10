package ui.commands;

@FunctionalInterface
public interface Command {

    public static final String CANCEL = "cancel";
    public static final String CANCEL_PROMPT = " (or \"" + CANCEL + "\" to return to the main menu):";

    /**
     * Executes the Command.
     */
    void execute();
}
