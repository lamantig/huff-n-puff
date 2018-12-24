package ui.commands;

public enum CommandKey {

    COMPARE("compare"),
    COMPRESS("compress"),
    DECOMPRESS("decompress"),
    QUIT("quit");

    private final String key;

    private CommandKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
