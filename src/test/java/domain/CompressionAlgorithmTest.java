package domain;

import io.FileUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompressionAlgorithmTest {

    @Test
    public void decompressingCompressedHuffmanFileRestoresItsOriginalContent() throws IOException {
        decompressingCompressedFileRestoresItsOriginalContent(new Huffman());
    }

    @Test
    public void decompressingCompressedLZWFileRestoresItsOriginalContent() throws IOException {
        decompressingCompressedFileRestoresItsOriginalContent(new LZW());
    }

    private void decompressingCompressedFileRestoresItsOriginalContent(CompressionAlgorithm algorithm) throws IOException {

        String fileName = "test/test.txt";
        Path originalFilePath = Paths.get(fileName).toRealPath();
        byte[] originalFile = FileUtils.readFile(originalFilePath);

        algorithm.compressFile(originalFilePath);
        Path compressedFilePath = Paths.get(fileName + algorithm.getExtension()).toRealPath();

        algorithm.decompressFile(compressedFilePath);
        byte[] decompressedFile = FileUtils.readFile(originalFilePath);

        assertArrayEquals(originalFile, decompressedFile);
    }
}
