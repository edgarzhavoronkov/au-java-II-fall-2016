package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 23.10.2016.
 */
public class CheckoutBranchFailException extends CommandFailException {
    public CheckoutBranchFailException(CoreException cause) {
        super(cause);
    }
}
