package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 05.11.2016.
 */
public class ServerException extends RuntimeException {
    public ServerException(String message) {
        super(message);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }
}
