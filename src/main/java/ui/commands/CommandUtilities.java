package ui.commands;

import io.IO;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static ui.commands.Command.CANCEL;
import static ui.commands.Command.CANCEL_PROMPT;

/**
 * Utility class with common methods for Command implementations.
 */
public final class CommandUtilities {

    /**
     * Asks for a valid Path using the given IO.
     *
     * @param io IO used for communication.
     * @param action String used to personalize the prompt with the action that
     * will be performed by the calling method with the returned Path.
     * @return A valid Path, or null if the operation was canceled by the user.
     */
    public static Path askForPath(IO io, String action) {

        io.println("please enter the path of the file to be " + action + CANCEL_PROMPT + "\n");

        String input = io.getInput().trim();
        if (input.equals(CANCEL)) {
            return null;
        }

        Path originalFilePath;
        try {
            originalFilePath = Paths.get(input).toRealPath();
        } catch (IOException e) {
            io.println("\n" + e + "\ninvalid path!\n");
            return askForPath(io, action);
        }

        return originalFilePath;
    }
}
