package ui.commands;

import domain.CompressionAlgorithm;
import domain.Huffman;
import domain.LZW;
import io.IO;
import java.io.IOException;
import java.nio.file.Files;
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
        new LZW(),
        new LZW(16, 196663),
        new LZW(20, 393161)
    };

    public static final String CHOICE_LIST_FORMAT = "\t%-15s%s\n";

    private static final String PATH_PROMPT = "please enter the path of the ",
                                FILE_PATH_PROMPT = PATH_PROMPT + "file to be ",
                                DIRECTORY_PATH_PROMPT = "directory to be ",
                                WRONG_PATH = "the provided path does not point to a ";

    private static Path askForPath(IO io, String propmt) {

        io.println(propmt + CANCEL_PROMPT);

        String input = io.getInput().trim();
        if (input.equals(CANCEL)) {
            return null;
        }

        try {
            return Paths.get(input).toRealPath();
        } catch (IOException e) {
            io.println(e + "\ninvalid path!\n");
            return askForPath(io, propmt);
        }
    }

    /**
     * Using the given IO, asks for a valid path pointing to a file.
     *
     * @param io IO used for communication.
     * @param action String used to personalize the prompt with the action that
     * will be performed by the calling method with the returned path.
     * @return A valid file path, or null if the operation was canceled by the
     * user.
     */
    public static Path askForFilePath(IO io, String action) {

        Path path = askForPath(io, FILE_PATH_PROMPT + action);

        if (path == null) {
            return null;
        }

        if (Files.isRegularFile(path)) {
            return path;
        } else {
            io.println(WRONG_PATH + "file!\n");
            return askForFilePath(io, action);
        }
    }

    /**
     * Using the given IO, asks for a valid path pointing to a directory.
     *
     * @param io IO used for communication.
     * @param action String used to personalize the prompt with the action that
     * will be performed by the calling method with the returned path.
     * @return A valid directory path, or null if the operation was canceled by
     * the user.
     */
    public static Path askForDirectoryPath(IO io, String action) {

        Path path = askForPath(io, DIRECTORY_PATH_PROMPT + action);

        if (path == null) {
            return null;
        }

        if (Files.isDirectory(path)) {
            return path;
        } else {
            io.println(WRONG_PATH + "directory!\n");
            return askForDirectoryPath(io, action);
        }
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
        for (CompressionAlgorithm a : ALGORITHMS) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Returns the compression algorithm corresponding to the given file
     * extension.
     *
     * @param extension Extension of a compressed file.
     * @return Compression algorithm corresponding to the given file extension.
     */
    public static CompressionAlgorithm getAlgorithmByExtension(String extension) {
        for (CompressionAlgorithm a : ALGORITHMS) {
            if (a.getExtension().equals(extension)) {
                return a;
            }
        }
        return null;
    }
}
