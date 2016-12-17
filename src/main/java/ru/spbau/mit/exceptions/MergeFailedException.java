package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 04.10.2016.
 * Exception which fires when merge failed
 */
public class MergeFailedException extends CommandFailException {
    public MergeFailedException(String message) {
        super(message);
    }
}
