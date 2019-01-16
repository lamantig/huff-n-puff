package ui.commands;

import domain.BitSequence;
import domain.CompressionAlgorithm;
import domain.Utils;
import io.FileUtils;
import io.IO;
import java.nio.file.Path;

/**
 * A Command for comparing the performance of different compression algorithms.
 */
public class Compare extends BasicCommand {

    public static final String KEY = "compare";

    private static final String SIMPLE = "simple";
    private static final String THOROUGH = "thorough";
    private static final String ERROR_MSG = "an error occurred while ";
    private static final String READING_ERROR = ERROR_MSG + "reading file ";
    private static final String WRITING_ERROR = ERROR_MSG + "writing file ";
    private static final String COMP_DECOMP_ERROR = ERROR_MSG + "compressing and decompressing file ";
    private static final String WITH = " with algorithm ";
    private static final String STATS_FORMAT = "\t%-15s%9.2f%13d%13d\n";

    /**
     * Creates an instance of Compare.
     *
     * @param io An IO to be used for communication.
     */
    public Compare(IO io) {
        super(io, "compare the performance of different compression algorithms");
    }

    @Override
    public void execute() {

        io.println("please enter the comparison mode you wish to use" + CANCEL_PROMPT);
        io.printf(CommandUtils.CHOICE_LIST_FORMAT, SIMPLE,
                "compress and decompress a single file once, "
                + "comparing time elapsed and data compression ratio");
        io.printf(CommandUtils.CHOICE_LIST_FORMAT, THOROUGH, "more elaborate stats");

        switch (io.getInput().trim()) {
            case CANCEL:
                return;
            case SIMPLE:
                simpleComparison();
                break;
            case THOROUGH:
                thoroughComparison();
                break;
            default:
                io.println("unsupported comparison mode\n");
                execute();
                break;
        }
    }

    private void simpleComparison() {

        Path originalFilePath = CommandUtils.askForPath(io, "used for comparing algorithms");
        if (originalFilePath == null) {
            return;
        }

        byte[] originalData = FileUtils.readFile(originalFilePath);
        if (originalData == null) {
            io.println(READING_ERROR + originalFilePath + "\n");
            return;
        }

        io.printf("\t%-15s%11s%17s%17s\n", "algorithm", "d. c. ratio", "compr. time", "decompr. time");
        io.println("\t------------------------------------------------------------");
        for (CompressionAlgorithm algorithm : CommandUtils.ALGORITHMS) {

            long compressionStartingTime = System.nanoTime();
            BitSequence compressedDataBitSeq = algorithm.compressData(originalData);
            long compressionElapsedTime = System.nanoTime() - compressionStartingTime;

            Path compressedFilePath = originalFilePath.resolveSibling(
                originalFilePath.getFileName() + algorithm.getExtension());

            if (!FileUtils.writeFile(compressedFilePath, compressedDataBitSeq.getBits(),
                    compressedDataBitSeq.getLengthInBytes())) {
                io.println(WRITING_ERROR + compressedFilePath + "\n");
                return;
            }

            byte[] compressedData = FileUtils.readFile(compressedFilePath);
            if (compressedData == null) {
                io.println(READING_ERROR + compressedFilePath + "\n");
                return;
            }

            long decompressionStartingTime = System.nanoTime();
            byte[] decompressedData = algorithm.decompressData(compressedData);
            long decompressionElapsedTime = System.nanoTime() - decompressionStartingTime;

            if (!Utils.equals(originalData, decompressedData)) {
                io.println(COMP_DECOMP_ERROR + originalFilePath + WITH + algorithm.getName() + "\n");
                return;
            }

            double compressionRatio = (double) decompressedData.length / compressedData.length;
            io.printf("\t%-15s%11.3f%17d%17d\n", algorithm.getName(),
                    compressionRatio, compressionElapsedTime, decompressionElapsedTime);
        }
        io.println("");
    }

    private void thoroughComparison() {
        io.println("this comparison mode hasn't been implemented yet\n");
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
