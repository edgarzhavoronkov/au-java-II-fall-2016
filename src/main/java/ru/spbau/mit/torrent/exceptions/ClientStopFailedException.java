package ru.spbau.mit.torrent.exceptions;

/**
 * Created by Эдгар on 05.12.2016.
 */
public class ClientStopFailedException extends ServerStopFailException {
    public ClientStopFailedException(Throwable cause) {
        super(cause);
    }
}
