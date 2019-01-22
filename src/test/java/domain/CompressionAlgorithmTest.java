package domain;

import io.FileUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Disabled;

public class CompressionAlgorithmTest {

    private static final String TEST_TEXT_FILE_PATH = "test/test.txt";
    private static final String NONEXISTENT_FILE_PATH = "nonexistent";
    private static final int[] LENGTH_CHANGE_INDEXES = {255, 768, 1793, 3842};
    private static final int UNUSED_VALUES = LENGTH_CHANGE_INDEXES.length;

    @Test
    public void decompressingCompressedHuffmanFileRestoresItsOriginalContent() throws IOException {
        assertTrue(decompressingCompressedFileRestoresItsOriginalContent(new Huffman()));
    }

    @Test
    public void decompressingCompressedLZWFileRestoresItsOriginalContent() throws IOException {
        assertTrue(decompressingCompressedFileRestoresItsOriginalContent(new LZW()));
    }

    @Test
    public void huffmanWorksWhenDataContainsAllPossibleByteValuesWithDifferentProbabilities() {
        int length = 700;
        byte[] originalData = new byte[length];
        byte b = Byte.MAX_VALUE;
        for (int i = 0; i < length / 2; i++) {
            originalData[i] = b--;
        }
        assertTrue(decompressingCompressedDataRestoresItsOriginalContent(new Huffman(), originalData));
    }

    @Test
    public void huffmanWorksWithSpecialCaseWhenAllProbabilitiesAreEqual() {
        byte[] badData = new byte[Utils.POSSIBLE_BYTE_VALUES_COUNT];
        byte b = Byte.MIN_VALUE;
        for (int i = 0; i < badData.length; i++) {
            badData[i] = b++;
        }
        assertTrue(decompressingCompressedDataRestoresItsOriginalContent(new Huffman(), badData));
    }

    @Test
    public void lzwWorksWithSpecialCaseRightBeforeLengthChangesAndDictionaryReset() {
        assertTrue(lzwWorksWithSpecialCaseNearLengthChangesWithOffset(0));
    }

    @Test
    public void lzwWorksWithSpecialCaseRightAfterLengthChangesAndDictionaryReset() {
        assertTrue(lzwWorksWithSpecialCaseNearLengthChangesWithOffset(1));
    }

    @Disabled("takes too much time (about half a minute)")
    @Test
    public void lzwWorksWithLargeArrayContainingSingleValue() {

        int length = 7_500_000;
        byte[] originalData = new byte[length];
        Arrays.fill(originalData, 0, length, (byte) 19);

        decompressingCompressedDataRestoresItsOriginalContent(new LZW(), originalData);
    }

    @Test
    public void compressFileReturnsNegativeValueWhenGivenNonexistentFilePath() {
        assertTrue(new Huffman().compressFile(Paths.get(NONEXISTENT_FILE_PATH)) < 0);
    }

    @Test
    public void decompressFileReturnsNegativeValueWhenGivenNonexistentFilePath() {
        assertTrue(new LZW().decompressFile(Paths.get(NONEXISTENT_FILE_PATH)) < 0);
    }

    private boolean decompressingCompressedFileRestoresItsOriginalContent(
            CompressionAlgorithm algorithm) throws IOException {

        String fileName = TEST_TEXT_FILE_PATH;
        Path originalFilePath = Paths.get(fileName).toRealPath();
        byte[] originalFile = FileUtils.readFile(originalFilePath);

        algorithm.compressFile(originalFilePath);
        Path compressedFilePath = Paths.get(fileName + algorithm.getExtension()).toRealPath();

        algorithm.decompressFile(compressedFilePath);
        byte[] decompressedFile = FileUtils.readFile(originalFilePath);

        return Arrays.equals(originalFile, decompressedFile);
    }

    private boolean decompressingCompressedDataRestoresItsOriginalContent(
            CompressionAlgorithm algorithm, byte[] originalData) {

        BitSequence compressedDataBitSeq = algorithm.compressData(originalData);

        int compressedDataLength = compressedDataBitSeq.getLengthInBytes();
        byte[] compressedData = new byte[compressedDataLength];
        System.arraycopy(compressedDataBitSeq.getBits(), 0, compressedData, 0, compressedDataLength);

        byte[] decompressedData = algorithm.decompressData(compressedData);

        return Arrays.equals(originalData, decompressedData);
    }

    private boolean lzwWorksWithSpecialCaseNearLengthChangesWithOffset(int specialCaseOffset) {

        byte[] badData = new byte[4600];
        int i = 0;
        byte unusedValue = (byte) (Byte.MAX_VALUE - UNUSED_VALUES);
        byte a = Byte.MIN_VALUE;
        byte b = a;
        byte bReset = ++b;

        while (b < unusedValue && i < 4200) {

            while (b < unusedValue) {
                badData[i++] = a;
                badData[i++] = b++;
            }

            a++;
            b = ++bReset;
        }

        for (int j : LENGTH_CHANGE_INDEXES) {

            for (int k = specialCaseOffset; k < specialCaseOffset + 3; k++) {
                badData[j + k] = unusedValue;
            }

            unusedValue++;
        }

        return decompressingCompressedDataRestoresItsOriginalContent(new LZW(), badData);
    }
}
