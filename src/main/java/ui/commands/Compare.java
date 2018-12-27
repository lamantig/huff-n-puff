package ui.commands;

import io.IO;

/**
 * A Command for comparing the performance of different compression algorithms.
 */
public class Compare extends BasicCommand {

    /**
     * Creates an instance of Compare.
     *
     * @param io An IO to be used for communication.
     */
    public Compare(IO io) {
        super(io, "compare the performance of different compression algorithms");
    }
}
