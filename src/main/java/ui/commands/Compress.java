package ui.commands;

import domain.CompressionAlgorithm;
import domain.Huffman;
import io.IO;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * A Command for compressing a file.
 */
public class Compress extends BasicCommand {

    private final Map<String, CompressionAlgorithm> compressionAlgorithms;

    /**
     * Creates an instance of Compress.
     *
     * @param io An IO to be used for communication.
     */
    public Compress(IO io) {
        super(io, "compress a file using an algorithm of your choice");
        this.compressionAlgorithms = new HashMap<>();
        compressionAlgorithms.put(HUFFMAN, new Huffman());
    }

    /**
     * Executes this Compress Command.
     */
    @Override
    public void execute() {

        Path originalFilePath = CommandUtilities.askForPath(io, "compressed");
        if (originalFilePath == null) {
            return;
        }

        CompressionAlgorithm algorithm = askForAlgorithm();
        if (algorithm == null) {
            return;
        }

        if (algorithm.compressFile(originalFilePath) >= 0) {
            io.println("\nfile compression completed successfully!");
        } else {
            io.println("\nERROR! file compression didn't complete.");
        }
    }

    /**
     * Asks for a CompressionAlgorithm to be chosen from those available.
     *
     * @return A CompressionAlgorithm, or null if the operation was canceled by
     * the user.
     */
    private CompressionAlgorithm askForAlgorithm() {

        io.println("\nplease enter the compression algorithm to be used" + CANCEL_PROMPT);
        compressionAlgorithms.entrySet().stream()
                .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                .forEachOrdered(e -> {
                    io.printf("\t%-15s%s\n", e.getKey(), e.getValue().getDescription());
                });
        io.println("");

        String input = io.getInput().toLowerCase().trim();
        if (input.equals(CANCEL)) {
            return null;
        }

        CompressionAlgorithm algorithm = compressionAlgorithms.get(input);
        if (algorithm == null) {
            io.println("\nunsupported algorithm!");
            return askForAlgorithm();
        }

        return algorithm;
    }
}
