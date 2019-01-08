package ui;

import io.IO;
import ui.commands.BasicCommand;
import ui.commands.Command;
import ui.commands.Compare;
import ui.commands.Compress;
import ui.commands.Decompress;
import ui.commands.Quit;

/**
 * A textual user interface.
 */
public class TUI {

    private final IO io;
    private final BasicCommand[] commands = new BasicCommand[4];
    private final Compare compare;
    private final Compress compress;
    private final Decompress decompress;
    private final Quit quit;

    /**
     * Creates an instance of TUI (textual user interface).
     *
     * @param io An IO to be used by this TUI for input and output.
     */
    public TUI(IO io) {
        this.io = io;
        int i = -1;
        compare = new Compare(io);
        commands[++i] = compare;
        compress = new Compress(io);
        commands[++i] = compress;
        decompress = new Decompress(io);
        commands[++i] = decompress;
        quit = new Quit(io);
        commands[++i] = quit;
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
            for (BasicCommand c : commands) {
                String key = c.getKey();
                io.printf("\t%-15s%s\n", key, c.toString());
            }

            input = io.getInput().toLowerCase().trim();

            switch (input) {
                case Compare.KEY:
                    command = compare;
                    break;
                case Compress.KEY:
                    command = compress;
                    break;
                case Decompress.KEY:
                    command = decompress;
                    break;
                case Quit.KEY:
                    command = quit;
                    break;
                default:
                    command = unsupported;
            }

            command.execute();

        } while (!input.equals(Quit.KEY));
    }
}
