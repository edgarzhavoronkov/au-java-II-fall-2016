package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 01.10.2016.
 * Wrapper for {@link java.io.IOException} which fires if deserialization fails
 */

public class ReadFailedException extends Throwable {
    public ReadFailedException(Exception e) {
        super(e);
    }
}
