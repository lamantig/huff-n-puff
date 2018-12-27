package ui.commands;

import io.IO;

/**
 * A Command for decompressing a file.
 */
public class Decompress extends BasicCommand {

    /**
     * Creates an instance of Decompress.
     *
     * @param io An IO to be used for communication.
     */
    public Decompress(IO io) {
        super(io, "decompress a file using an algorithm of your choice "
                + "(must be the same you used for compression)");
    }
}
