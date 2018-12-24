package io;

public interface IO {

    /**
     * Prints the given String.
     *
     * @param s The String to be printed.
     */
    void print(String s);

    /**
     * Prints the given String followed by a newline.
     *
     * @param s The String to be printed.
     */
    default void println(String s) {
        print(s + "\n");
    }

    /**
     * Prints a String with the given format, using the given arguments.
     *
     * @param format The format of the String to be printed.
     * @param args The arguments to be substituted into the format String.
     */
    void printf(String format, Object... args);

    /**
     * Gets an input String.
     *
     * @return A String from this IO's input source.
     */
    String getInput();
}
