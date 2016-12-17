package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 23.10.2016.
 */
public class CheckoutBranchFailException extends CommandFailException {
    public CheckoutBranchFailException(Throwable cause) {
        super(cause);
    }
}
