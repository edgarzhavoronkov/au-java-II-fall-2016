package ru.spbau.mit.torrent.exceptions;

import java.io.IOException;

/**
 * Created by Эдгар on 28.11.2016.
 */
public class TrackerStartFailException extends Throwable {
    public TrackerStartFailException(Throwable cause) {
        super(cause);
    }
}
