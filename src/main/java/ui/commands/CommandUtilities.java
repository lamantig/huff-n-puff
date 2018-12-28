package ui.commands;

import domain.CompressionAlgorithm;
import domain.Huffman;
import io.IO;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import static ui.commands.Command.CANCEL;
import static ui.commands.Command.CANCEL_PROMPT;
import static ui.commands.Command.HUFFMAN;

/**
 * Utility class with common methods for Command implementations.
 */
public final class CommandUtilities {

    public static final Map<String, CompressionAlgorithm> ALGORITHMS_BY_NAME;
    public static final Map<String, CompressionAlgorithm> ALGORITHMS_BY_EXTENSION;

    static {
        Map<String, CompressionAlgorithm> map = new HashMap<>();
        map.put(HUFFMAN, new Huffman());
        ALGORITHMS_BY_NAME = Collections.unmodifiableMap(map);
        map = ALGORITHMS_BY_NAME.values().stream()
                .collect(Collectors.toMap(alg -> alg.getExtension(), alg -> alg));
        ALGORITHMS_BY_EXTENSION = Collections.unmodifiableMap(map);
    }

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
     * Prints (to the given IO) the description of each CompressionAlgorithm in
     * the given Map, preceeded by the corresponding key.
     *
     * @param algorithms The Map whose keys and values are to be printed.
     * @param io The IO to be used for printing.
     */
    public static void printAlgorithmsWithKeys(Map<String, CompressionAlgorithm> algorithms, IO io) {

        algorithms.entrySet().stream()
                .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                .forEachOrdered(e -> {
                    io.printf("\t%-15s%s\n", e.getKey(), e.getValue().getDescription());
                });
    }
}
