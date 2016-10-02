package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 01.10.2016.
 * Wrapper for {@link java.io.IOException} which fires if serialization fails
 */
public class WriteFailedException extends Throwable {
    public WriteFailedException(Exception e) {
        super(e);
    }
}
