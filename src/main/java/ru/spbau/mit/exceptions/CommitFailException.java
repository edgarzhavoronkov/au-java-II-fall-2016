package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 24.10.2016.
 */
public class CommitFailException extends CommandFailException {
    public CommitFailException(CoreException cause) {
        super(cause);
    }
}
