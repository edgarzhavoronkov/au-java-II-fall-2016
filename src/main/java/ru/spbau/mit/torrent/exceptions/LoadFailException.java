package ru.spbau.mit.torrent.exceptions;

/**
 * Created by Эдгар on 07.12.2016.
 */
public class LoadFailException extends Exception {
    public LoadFailException(Throwable cause) {
        super(cause);
    }

    public LoadFailException(String message) {
        super(message);
    }
}
