package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 04.10.2016.
 * Wrapper for {@link Exception} which i throw if something goes wrong in {@link ru.spbau.mit.model.core.VcsCore}
 */
public class CoreException extends Exception {
    public CoreException(Throwable cause) {
        super(cause);
    }

    public CoreException(String message) {
        super(message);
    }
}
