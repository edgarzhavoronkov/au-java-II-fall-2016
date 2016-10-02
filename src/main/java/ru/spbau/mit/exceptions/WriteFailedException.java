package ru.spbau.mit.exceptions;

import java.io.IOException;

/**
 * Created by Эдгар on 01.10.2016.
 */
public class WriteFailedException extends Throwable {
    public WriteFailedException(Exception e) {
        super(e);
    }
}
