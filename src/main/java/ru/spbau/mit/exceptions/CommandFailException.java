package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 01.10.2016.
 * My own kind of exception which i throw if something goes wrong in command execution
 */
public class CommandFailException extends Throwable {
    public CommandFailException(String message) {
        super(message);
    }

    public CommandFailException(Throwable cause) {
        super(cause);
    }
}
