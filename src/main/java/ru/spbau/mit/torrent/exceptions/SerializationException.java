package ru.spbau.mit.torrent.exceptions;

import java.io.IOException;

/**
 * Created by Эдгар on 05.12.2016.
 */
public class SerializationException extends IOException {
    public SerializationException(Throwable cause) {
        super(cause);
    }
}
