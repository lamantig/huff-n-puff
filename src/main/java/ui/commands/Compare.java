package ui.commands;

import domain.BitSequence;
import domain.CompressionAlgorithm;
import domain.Utils;
import io.FileUtils;
import io.IO;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * A Command for comparing the performance of different compression algorithms.
 */
public class Compare extends BasicCommand {

    public static final String KEY = "compare";

    private static final String
            COMPARE_ACTION = "used for comparing algorithms",
            SIMPLE = "simple",
            THOROUGH = "thorough",
            STATS_HEADER = String.format("\t%-15s%9s%19s%19s\n%s", "algorithm",
                    "d. c. ratio", "compr. time", "decompr. time",
                    "\t------------------------------------------------------------"),
            STATS_FORMAT = "\t%-15s%9.3f%19d%19d\n",
            ERROR_MSG = "an error occurred while ",
            READING_ERROR = ERROR_MSG + "reading ",
            FILE_R_ERROR = READING_ERROR + "file ",
            DIR_READ_ERROR = READING_ERROR + "from directory ",
            FILE_W_ERROR = ERROR_MSG + "writing file ",
            FILE_COMP_DEC_E = ERROR_MSG + "compressing and decompressing file ",
            WITH = " with algorithm ";
    private static final int
            DEFAULT_REPS = 13,
            MIN_REPS = 1;

    private class Stats {
        private long originalFileSize;
        private long compressedFileSize;
        private long compressionElapsedTime;
        private long decompressionElapsedTime;
        private double compressionRatio() {
            return (double) originalFileSize / compressedFileSize;
        }
    }

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
        io.printf(CommandUtils.CHOICE_LIST_FORMAT + CommandUtils.CHOICE_LIST_FORMAT, SIMPLE,
                "compress and decompress a single file once with each algorithm, ",
                "", "comparing data compression ratio and time elapsed");
        io.printf(CommandUtils.CHOICE_LIST_FORMAT + CommandUtils.CHOICE_LIST_FORMAT, THOROUGH,
                "compress and decompress a directory's files many times with each algorithm, ",
                "", "comparing average data compression ratio and total time elapsed");

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

        Path originalFilePath = CommandUtils.askForFilePath(io, COMPARE_ACTION);
        if (originalFilePath == null) {
            return;
        }

        byte[] originalData = FileUtils.readFile(originalFilePath);
        if (originalData == null) {
            io.println(FILE_R_ERROR + originalFilePath + "\n");
            return;
        }

        Stats[] stats = new Stats[CommandUtils.ALGORITHMS.length];
        for (int i = 0; i < stats.length; i++) {
            Stats s = computeStats(CommandUtils.ALGORITHMS[i],
                    originalFilePath, originalData, 1);
            if (s == null) {
                return;
            }
            stats[i] = s;
        }

        printStats(stats);
    }

    private Stats computeStats(CompressionAlgorithm algorithm,
            Path originalFilePath, byte[] originalData, int reps) {

        Stats stats = new Stats();

        BitSequence compressedDataBitSeq;
        int i = 0;
        long compressionStartingTime = System.nanoTime();
        do {
            compressedDataBitSeq = algorithm.compressData(originalData);
        } while (++i < reps);
        stats.compressionElapsedTime = System.nanoTime() - compressionStartingTime;

        Path compressedFilePath = originalFilePath.resolveSibling(
                originalFilePath.getFileName() + algorithm.getExtension());

        if (!FileUtils.writeFile(compressedFilePath, compressedDataBitSeq.getBits(),
                compressedDataBitSeq.getLengthInBytes())) {
            io.println(FILE_W_ERROR + compressedFilePath + "\n");
            return null;
        }

        byte[] compressedData = FileUtils.readFile(compressedFilePath);
        if (compressedData == null) {
            io.println(FILE_R_ERROR + compressedFilePath + "\n");
            return null;
        }

        byte[] decompressedData;
        long decompressionStartingTime = System.nanoTime();
        do {
            decompressedData = algorithm.decompressData(compressedData);
        } while (--i > 0);
        stats.decompressionElapsedTime = System.nanoTime() - decompressionStartingTime;

        if (!Utils.equals(originalData, decompressedData)) {
            io.println(FILE_COMP_DEC_E + originalFilePath + WITH + algorithm.getName() + "\n");
            return null;
        }

        stats.originalFileSize = decompressedData.length;
        stats.compressedFileSize = compressedData.length;

        return stats;
    }

    private void printStats(Stats[] stats) {

        io.println(STATS_HEADER);

        for (int i = 0; i < stats.length; i++) {
            io.printf(STATS_FORMAT, CommandUtils.ALGORITHMS[i].getName(),
                    stats[i].compressionRatio(), stats[i].compressionElapsedTime,
                    stats[i].decompressionElapsedTime);
        }

        io.println("");
    }

    private void thoroughComparison() {

        Path directoryPath = CommandUtils.askForDirectoryPath(io, COMPARE_ACTION);
        if (directoryPath == null) {
            return;
        }

        Path[] filePaths = FileUtils.getFilePaths(directoryPath);
        if (filePaths == null) {
            io.println(DIR_READ_ERROR + directoryPath + "\n");
            return;
        }

        int reps = askForReps();

        Stats[][] stats = new Stats[CommandUtils.ALGORITHMS.length][filePaths.length];

        for (int j = 0; j < filePaths.length; j++) {

            Path originalFilePath = filePaths[j];

            byte[] originalData = FileUtils.readFile(originalFilePath);
            if (originalData == null) {
                io.println(FILE_R_ERROR + originalFilePath + "\n");
                return;
            }

            for (int i = 0; i < CommandUtils.ALGORITHMS.length; i++) {

                Stats s = computeStats(CommandUtils.ALGORITHMS[i],
                        originalFilePath, originalData, reps);
                if (s == null) {
                    return;
                }

                stats[i][j] = s;
            }
        }

        Stats[] summaryStats = sumStats(stats);

        printStats(summaryStats);
    }

    private int askForReps() {

        io.println("how many times should each file be compressed and decompressed? "
                + "(minimum is " + MIN_REPS + ", default is " + DEFAULT_REPS + ")");

        int reps = -1;
        boolean invalid = false;
        try {
            reps = Integer.parseInt(io.getInput().trim());
        } catch (NumberFormatException e) {
            invalid = true;
        }

        if (invalid || reps < MIN_REPS) {
            reps = DEFAULT_REPS;
            io.println("invalid value provided, will use default value (" + DEFAULT_REPS + ")\n");
        }

        return reps;
    }

    private Stats[] sumStats(Stats[][] stats) {

        Stats[] summaryStats = new Stats[stats.length];
        for (int i = 0; i < summaryStats.length; i++) {

            summaryStats[i] = Stream.of(stats[i]).reduce(new Stats(), (s1, s2) -> {
                s1.originalFileSize += s2.originalFileSize;
                s1.compressedFileSize += s2.compressedFileSize;
                s1.compressionElapsedTime += s2.compressionElapsedTime;
                s1.decompressionElapsedTime += s2.decompressionElapsedTime;
                return s1;
            });
        }

        return summaryStats;
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
