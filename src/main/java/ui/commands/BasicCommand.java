package ui.commands;

import io.IO;

public abstract class BasicCommand implements Command {

    final IO io;
    private final String description;

    /**
     * Creates an instance of BasicCommand.
     *
     * @param io The IO to be used for input and output.
     * @param description A description of the command's functionality.
     */
    public BasicCommand(IO io, String description) {
        this.io = io;
        this.description = description;
    }

    @Override
    public void execute() {
        io.println("this command hasn't been implemented yet");
    }

    @Override
    public String toString() {
        return description;
    }
}
