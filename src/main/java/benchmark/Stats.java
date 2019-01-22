package benchmark;

import domain.BitSequence;
import domain.CompressionAlgorithm;
import domain.Utils;
import io.FileUtils;
import io.IO;
import java.nio.file.Path;
import java.util.stream.Stream;
import static ui.commands.Compare.FILE_COMP_DEC_E;
import static ui.commands.Compare.FILE_R_ERROR;
import static ui.commands.Compare.FILE_W_ERROR;
import static ui.commands.Compare.WITH;

public class Stats {

    public long originalFileSize;
    public long compressedFileSize;
    public long compressionElapsedTime;
    public long decompressionElapsedTime;

    public double compressionRatio() {
        return (double) originalFileSize / compressedFileSize;
    }

    public static Stats computeStats(CompressionAlgorithm algorithm,
            Path originalFilePath, byte[] originalData, int reps, IO io) {

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

    public static Stats[] sumStats(Stats[][] stats) {

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
}
