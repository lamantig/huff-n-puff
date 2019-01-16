package ui.commands;

import domain.CompressionAlgorithm;
import domain.Huffman;
import domain.LZW;
import io.IO;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static ui.commands.Command.CANCEL;
import static ui.commands.Command.CANCEL_PROMPT;

/**
 * Utility class with common methods for Command implementations.
 */
public final class CommandUtils {

    public static final CompressionAlgorithm[] ALGORITHMS = new CompressionAlgorithm[]{
        new Huffman(),
        new LZW()
    };

    public static final String CHOICE_LIST_FORMAT = "\t%-15s%s\n";

    /**
     * Asks for a valid Path using the given IO.
     *
     * @param io IO used for communication.
     * @param action String used to personalize the prompt with the action that
     * will be performed by the calling method with the returned Path.
     * @return A valid Path, or null if the operation was canceled by the user.
     */
    public static Path askForPath(IO io, String action) {

        io.println("please enter the path of the file to be " + action + CANCEL_PROMPT);

        String input = io.getInput().trim();
        if (input.equals(CANCEL)) {
            return null;
        }

        Path originalFilePath;
        try {
            originalFilePath = Paths.get(input).toRealPath();
        } catch (IOException e) {
            io.println(e + "\ninvalid path!\n");
            return askForPath(io, action);
        }

        return originalFilePath;
    }

    /**
     * Prints (to the given IO) the description of each available
     * CompressionAlgorithm, preceeded by the algorithm's name.
     *
     * @param io The IO to be used for printing.
     */
    public static void printAlgorithmsWithNames(IO io) {
        for (CompressionAlgorithm a : ALGORITHMS) {
            io.printf(CHOICE_LIST_FORMAT, a.getName(), a.getDescription());
        }
    }

    /**
     * Prints (to the given IO) the description of each available
     * CompressionAlgorithm, preceeded by the algorithm's extension.
     *
     * @param io The IO to be used for printing.
     */
    public static void printAlgorithmsWithExtensions(IO io) {
        for (CompressionAlgorithm a : ALGORITHMS) {
            io.printf(CHOICE_LIST_FORMAT, a.getExtension(), a.getDescription());
        }
    }

    /**
     * Returns the compression algorithm corresponding to the given algorithm
     * name.
     *
     * @param name Name of the algorithm.
     * @return Compression algorithm corresponding to the given name.
     */
    public static CompressionAlgorithm getAlgorithmByName(String name) {
        switch (name) {
            case Huffman.NAME:
                return ALGORITHMS[0];
            case LZW.NAME:
                return ALGORITHMS[1];
            default:
                return null;
        }
    }

    /**
     * Returns the compression algorithm corresponding to the given file
     * extension.
     *
     * @param extension Extension of a compressed file.
     * @return Compression algorithm corresponding to the given file extension.
     */
    public static CompressionAlgorithm getAlgorithmByExtension(String extension) {
        switch (extension) {
            case Huffman.COMPRESSED_FILE_EXTENSION:
                return ALGORITHMS[0];
            case LZW.COMPRESSED_FILE_EXTENSION:
                return ALGORITHMS[1];
            default:
                return null;
        }
    }
}
