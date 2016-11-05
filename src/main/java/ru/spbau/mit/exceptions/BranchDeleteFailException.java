package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 23.10.2016.
 */
public class BranchDeleteFailException extends CommandFailException {
    public BranchDeleteFailException(Throwable cause) {
        super(cause);
    }
}
