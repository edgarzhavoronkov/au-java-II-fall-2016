package ru.spbau.mit.torrent.exceptions;

/**
 * Created by Эдгар on 07.12.2016.
 */
public class NoSeedsFoundException extends Exception {
    public NoSeedsFoundException(String message) {
        super(message);
    }
}
