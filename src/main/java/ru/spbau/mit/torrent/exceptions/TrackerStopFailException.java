package ru.spbau.mit.torrent.exceptions;

/**
 * Created by Эдгар on 28.11.2016.
 */
public class TrackerStopFailException extends ServerStopFailException {
    public TrackerStopFailException(Throwable cause) {
        super(cause);
    }
}
