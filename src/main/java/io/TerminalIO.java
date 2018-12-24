package io;

import java.util.Scanner;

/**
 * IO implementation which uses the terminal for input and output.
 */
public class TerminalIO implements IO {

    private final Scanner scanner;

    /**
     * Creates a new instance of TerminalIO.
     */
    public TerminalIO() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void print(String s) {
        System.out.print(s);
    }

    @Override
    public String getInput() {
        return scanner.nextLine();
    }

    @Override
    public void printf(String format, Object... args) {
        System.out.printf(format, args);
    }


}
