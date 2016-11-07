package ru.spbau.mit.exceptions;

/**
 * Created by Эдгар on 07.11.2016.
 */
public class AddFailException extends CommandFailException {
    public AddFailException(String message) {
        super(message);
    }
}
