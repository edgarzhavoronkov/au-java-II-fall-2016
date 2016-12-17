package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 05.11.2016.
 */
public class RepositoryException extends Throwable {
    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(Throwable cause) {
        super(cause);
    }
}
