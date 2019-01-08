package ui.commands;

import io.IO;

/**
 * A Command to terminate program execution.
 */
public class Quit extends BasicCommand {

    public static final String KEY = "quit";

    /**
     * Creates an instance of Quit.
     *
     * @param io An IO to be used for communication.
     */
    public Quit(IO io) {
        super(io, "terminate program execution");
    }

    @Override
    public void execute() {
        io.println("terminating program execution... seeya!");
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
