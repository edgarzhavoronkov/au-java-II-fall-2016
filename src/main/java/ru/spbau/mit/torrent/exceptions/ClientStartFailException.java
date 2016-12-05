package ru.spbau.mit.torrent.exceptions;

/**
 * Created by Эдгар on 05.12.2016.
 */
public class ClientStartFailException extends ServerStartFailException {
    public ClientStartFailException(Throwable cause) {
        super(cause);
    }
}
