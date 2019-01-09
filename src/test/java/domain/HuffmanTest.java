package domain;

import io.FileUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HuffmanTest {

    private Huffman huffman;

    @Test
    public void decompressingCompressedFileRestoresItsOriginalContent() throws IOException {

        huffman = new Huffman();
        String fileName = "test/test.txt";

        Path originalFilePath = Paths.get(fileName).toRealPath();
        byte[] originalFile = FileUtils.readFile(originalFilePath);

        huffman.compressFile(originalFilePath);
        Path compressedFilePath = Paths.get(fileName + Huffman.COMPRESSED_FILE_EXTENSION).toRealPath();

        huffman.decompressFile(compressedFilePath);
        byte[] decompressedFile = FileUtils.readFile(originalFilePath);

        assertArrayEquals(originalFile, decompressedFile);
    }
}
