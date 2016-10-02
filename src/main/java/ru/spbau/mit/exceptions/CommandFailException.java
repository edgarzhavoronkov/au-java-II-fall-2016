package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 01.10.2016.
 */
public class CommandFailException extends RuntimeException {
    public CommandFailException(String message) {
        super(message);
    }
}
