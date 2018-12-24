package ui.commands;

import io.IO;

/**
 * A Command for compressing a file.
 */
public class Compress extends BasicCommand {

    /**
     * Creates an instance of Compress.
     *
     * @param io An IO to be used for input and output.
     */
    public Compress(IO io) {
        super(io, "compress a file using an algorithm of your choice");
    }
}
