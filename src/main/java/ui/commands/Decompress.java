package ui.commands;

import domain.CompressionAlgorithm;
import io.IO;
import java.nio.file.Path;

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
        super(io, "decompress a file "
                + "(using the same algorithm that was used for compression)");
    }

    /**
     * Executes this Decompress Command.
     */
    @Override
    public void execute() {

        Path compressedFilePath = CommandUtilities.askForPath(io, "decompressed");
        if (compressedFilePath == null) {
            return;
        }

        CompressionAlgorithm algorithm = selectAlgorithm(compressedFilePath);
        if (algorithm == null) {
            io.println("unfortunately we cannot decompress the file (or directory) "
                    + "corresponding to the path you just entered!\n"
                    + "we can decompress files with the following extensions:");
            CommandUtilities.printAlgorithmsWithKeys(
                    CommandUtilities.ALGORITHMS_BY_EXTENSION, io);
            io.println("");
            return;
        }

        if (algorithm.decompressFile(compressedFilePath) >= 0) {
            io.println("file decompression completed successfully!\n");
        } else {
            io.println("ERROR! file decompression didn't complete\n");
        }
    }

    /**
     * Selects the algorithm that was used for compression, deducing it on the
     * basis of the compressed file's extension.
     *
     * @param compressedFilePath The compressed file's Path (from which file
     * extension can be extracted).
     * @return The CompressionAlgorithm corresponding to the file extension of
     * the given Path, or null if no CompressionAlgorithm was found.
     */
    private CompressionAlgorithm selectAlgorithm(Path compressedFilePath) {

        String fileExtension = extractFileExtension(compressedFilePath);
        if (fileExtension == null) {
            return null;
        }

        return CommandUtilities.ALGORITHMS_BY_EXTENSION.get(fileExtension);
    }

    /**
     * Extracts a file extension (including the period before the extension)
     * from a Path, if it ends with a file extension.
     *
     * @param path The Path from which the extension will be extracted.
     * @return A String corresponding to a file extension (including the period
     * before the extension), or null if no extension was found.
     */
    private String extractFileExtension(Path path) {

        String pathString = path.toString();

        int extensionStartingIndex = pathString.lastIndexOf(".");
        if (extensionStartingIndex < 0) {
            return null;
        }

        return pathString.substring(extensionStartingIndex);
    }
}
