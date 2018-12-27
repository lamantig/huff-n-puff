package io;

public interface IO {

    /**
     * Prints the given String.
     *
     * @param o The Object to be printed.
     */
    void print(Object o);

    /**
     * Prints the given String followed by a newline.
     *
     * @param o The Object to be printed.
     */
    default void println(Object o) {
        print(o + "\n");
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
