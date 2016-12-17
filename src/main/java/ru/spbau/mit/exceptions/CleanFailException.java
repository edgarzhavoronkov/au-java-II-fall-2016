package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 05.11.2016.
 */
public class CleanFailException extends CommandFailException {
    public CleanFailException(Throwable cause) {
        super(cause);
    }
}
