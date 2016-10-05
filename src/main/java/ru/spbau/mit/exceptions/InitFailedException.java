package ru.spbau.mit.exceptions;

/**
 * Created by edgar on 05.10.16.
 * Wrapper around {@link Exception} which is thrown if init of repository fails
 */
public class InitFailedException extends Exception {
    public InitFailedException(String message) {
        super(message);
    }

    public InitFailedException(CoreException cause) {
        super(cause);
    }
}
