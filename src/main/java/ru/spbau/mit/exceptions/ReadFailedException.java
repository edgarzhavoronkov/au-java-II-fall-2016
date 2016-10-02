package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 01.10.2016.
 */
public class ReadFailedException extends Throwable {
    public ReadFailedException(Exception e) {
        super(e);
    }
}
