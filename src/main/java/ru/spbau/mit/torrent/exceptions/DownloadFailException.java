package ru.spbau.mit.torrent.exceptions;

/**
 * Created by Эдгар on 07.12.2016.
 */
public class DownloadFailException extends RuntimeException {
    public DownloadFailException(Throwable cause) {
        super(cause);
    }
}
