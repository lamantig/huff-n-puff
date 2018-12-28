package ui;

import io.IO;
import java.util.HashMap;
import java.util.Map;
import ui.commands.Command;
import ui.commands.CommandKey;
import ui.commands.Compare;
import ui.commands.Compress;
import ui.commands.Decompress;
import ui.commands.Quit;

/**
 * A textual user interface.
 */
public class TUI {

    private final IO io;
    private final Map<String, Command> commands;

    /**
     * Creates an instance of TUI (textual user interface).
     *
     * @param io An IO to be used by this TUI for input and output.
     */
    public TUI(IO io) {
        this.io = io;
        this.commands = new HashMap<>();
        commands.put(CommandKey.COMPARE.getKey(), new Compare(io));
        commands.put(CommandKey.COMPRESS.getKey(), new Compress(io));
        commands.put(CommandKey.DECOMPRESS.getKey(), new Decompress(io));
        commands.put(CommandKey.QUIT.getKey(), new Quit(io));
    }

    /**
     * Runs (executes) this TUI.
     */
    public void run() {

        String input;
        Command command;
        Command unsupported = () -> io.println("unsupported command\n");

        io.println("\nwelcome to HUFF n PUFF!\n");

        do {
            io.println("supported commands:");
            for (CommandKey commKey : CommandKey.values()) {
                String key = commKey.getKey();
                io.printf("\t%-15s%s\n", key, commands.get(key).toString());
            }

            input = io.getInput().toLowerCase().trim();

            command = commands.getOrDefault(input, unsupported);
            command.execute();

        } while (!input.equals(CommandKey.QUIT.getKey()));
    }
}
