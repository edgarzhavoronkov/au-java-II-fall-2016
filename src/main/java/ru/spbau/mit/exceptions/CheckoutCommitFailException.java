package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 23.10.2016.
 */
public class CheckoutCommitFailException extends CommandFailException {
    public CheckoutCommitFailException(Throwable cause) {
        super(cause);
    }
}
