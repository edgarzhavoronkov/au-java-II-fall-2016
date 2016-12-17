package ru.spbau.mit.exceptions;

import java.io.FileNotFoundException;

/**
 * Created by Эдгар on 17.12.2016.
 */
public class RemoveFailException extends CommandFailException {
    public RemoveFailException(Throwable cause) {
        super(cause);
    }
}
