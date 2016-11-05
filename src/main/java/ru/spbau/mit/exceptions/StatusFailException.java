package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 24.10.2016.
 */
public class StatusFailException extends CommandFailException {
    public StatusFailException(Throwable cause) {
        super(cause);
    }
}
