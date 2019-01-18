package ui.commands;

import domain.CompressionAlgorithm;
import io.IO;
import java.nio.file.Path;

/**
 * A Command for compressing a file.
 */
public class Compress extends BasicCommand {

    public static final String KEY = "compress";

    /**
     * Creates an instance of Compress.
     *
     * @param io An IO to be used for communication.
     */
    public Compress(IO io) {
        super(io, "compress a file using an algorithm of your choice");
    }

    @Override
    public void execute() {

        Path originalFilePath = CommandUtils.askForFilePath(io, "compressed");
        if (originalFilePath == null) {
            return;
        }

        CompressionAlgorithm algorithm = askForAlgorithm();
        if (algorithm == null) {
            return;
        }

        if (algorithm.compressFile(originalFilePath) >= 0) {
            io.println("file compression completed successfully!\n");
        } else {
            io.println("ERROR! file compression didn't complete\n");
        }
    }

    /**
     * Asks for a CompressionAlgorithm to be chosen from those available.
     *
     * @return A CompressionAlgorithm, or null if the operation was canceled by
     * the user.
     */
    private CompressionAlgorithm askForAlgorithm() {

        io.println("please enter the compression algorithm to be used" + CANCEL_PROMPT);
        CommandUtils.printAlgorithmsWithNames(io);

        String input = io.getInput().toLowerCase().trim();
        if (input.equals(CANCEL)) {
            return null;
        }

        CompressionAlgorithm algorithm = CommandUtils.getAlgorithmByName(input);
        if (algorithm == null) {
            io.println("unsupported algorithm!\n");
            return askForAlgorithm();
        }

        return algorithm;
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
