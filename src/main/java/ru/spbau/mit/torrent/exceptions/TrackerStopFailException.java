package ru.spbau.mit.torrent.exceptions;

import java.io.IOException;

/**
 * Created by Эдгар on 28.11.2016.
 */
public class TrackerStopFailException extends Throwable {
    public TrackerStopFailException(Throwable cause) {
        super(cause);
    }
}
