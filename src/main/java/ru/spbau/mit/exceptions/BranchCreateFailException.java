package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 23.10.2016.
 */
public class BranchCreateFailException extends CommandFailException {
    public BranchCreateFailException(CoreException cause) {
        super(cause);
    }
}
