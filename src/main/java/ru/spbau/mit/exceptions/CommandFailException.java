package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 01.10.2016.
 * Wrapper for {@link RuntimeException} which i throw if something goes wrong in command execution
 */
public class CommandFailException extends RuntimeException {
    public CommandFailException(String message) {
        super(message);
    }

    public CommandFailException(Exception cause) {
        super(cause);
    }
}
