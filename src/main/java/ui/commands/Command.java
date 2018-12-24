package ui.commands;

@FunctionalInterface
public interface Command {

    /**
     * Executes the Command.
     */
    void execute();
}
