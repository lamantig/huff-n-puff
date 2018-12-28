package domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A utility class with File access methods.
 */
public class FileUtilities {

    /**
     * Reads a file to a byte array.
     *
     * @param path Path of the file to be read.
     * @return A byte array containing the file's data, or null if the reading
     * operation was unsuccessful.
     */
    public static byte[] readFile(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Writes data from a byte array into a file.
     *
     * @param path Path of the file to be written.
     * @param data A byte array containing the data to be written into the file.
     * @return True if the writing operation was successful, false otherwise.
     */
    public static boolean writeFile(Path path, byte[] data) {
        try {
            Files.write(path, data);
            return true;
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
    }

    /**
     * Removes the last N characters from the String representation of the given
     * Path (where N is given by tailLength) and returns the new Path obtained
     * from the truncated String.
     *
     * @param path The original Path.
     * @param tailLength The number of characters to cut.
     * @return The resulting Path after the cutting.
     */
    public static Path cutPathTail(Path path, int tailLength) {
        if (tailLength <= 0) {
            return path;
        }
        String pathString = path.toString();
        String cutPathString = pathString.substring(0, pathString.length() - tailLength);
        return Paths.get(cutPathString);
    }
}
