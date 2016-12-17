package ru.spbau.mit.exceptions;

import java.io.IOException;

/**
 * Created by Эдгар on 24.10.2016.
 */
public class ResetFailException extends CommandFailException {
    public ResetFailException(Throwable cause) {
        super(cause);
    }
}
