package main;

import io.IO;
import io.TerminalIO;
import ui.TUI;

public class App {

    /**
     * The main method of this program.
     * It doesn't use any of the provided arguments.
     *
     * @param args Arguments (unused).
     */
    public static void main(String[] args) {
        IO io = new TerminalIO();
        TUI tui = new TUI(io);
        tui.run();
    }
}
